package com.tramasys.auth.application.service;

import com.tramasys.auth.application.dto.request.LoginRequest;
import com.tramasys.auth.application.dto.request.RefreshTokenRequest;
import com.tramasys.auth.application.dto.request.RegisterRequest;
import com.tramasys.auth.application.dto.response.AuthResponse;
import com.tramasys.auth.application.dto.response.UserResponse;
import com.tramasys.auth.domain.exception.AuthenticationException;
import com.tramasys.auth.domain.exception.DuplicateResourceException;
import com.tramasys.auth.domain.exception.NotFoundException;
import com.tramasys.auth.domain.model.Permission;
import com.tramasys.auth.domain.model.RefreshToken;
import com.tramasys.auth.domain.model.Role;
import com.tramasys.auth.domain.model.User;
import com.tramasys.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.tramasys.auth.domain.port.out.RoleRepositoryPort;
import com.tramasys.auth.domain.port.out.UserRepositoryPort;
import com.tramasys.auth.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.tramasys.auth.domain.port.out.MediaStoragePort;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.tramasys.auth.domain.model.TramasysService;
import java.util.List;

@Service
@Transactional
public class AuthService {

    private final UserRepositoryPort userRepo;
    private final RoleRepositoryPort roleRepo;
    private final RefreshTokenRepositoryPort refreshRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;
    private final MediaStoragePort mediaPort;

    public AuthService(UserRepositoryPort userRepo,
            RoleRepositoryPort roleRepo,
            RefreshTokenRepositoryPort refreshRepo,
            PasswordEncoder encoder,
            JwtUtil jwt, MediaStoragePort mediaPort) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.refreshRepo = refreshRepo;
        this.encoder = encoder;
        this.jwt = jwt;
        this.mediaPort = mediaPort;
    }

    /* --------------------------- REGISTER --------------------------- */

    @Transactional // Seule cette partie verrouille la base de données
    protected User saveUserWithData(RegisterRequest request, UUID photoId, String photoUri) {
        Set<Role> roles = new HashSet<>();
        List<String> requestedRoles = request.getRoles() == null || request.getRoles().isEmpty() ? 
                                    List.of("USER") : request.getRoles();

        for (String roleName : requestedRoles) {
            Role role = roleRepo.findByName(roleName)
                    .orElseThrow(() -> new NotFoundException("Unknown role: " + roleName));
            roles.add(role);
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(encoder.encode(request.getPassword()))
                .service(request.getService())
                .photoId(photoId)    // On utilise les données de l'upload
                .photoUri(photoUri)
                .enabled(true)
                .createdAt(Instant.now())
                .roles(roles)
                .build();

        return userRepo.save(user);
    }

    public AuthResponse register(RegisterRequest request, MultipartFile photoFile) {
        // 1. Validation de base (Hors transaction)
        if (userRepo.existsByUsername(request.getUsername()))
            throw new DuplicateResourceException("Username already taken");

        // 2. Gestion de l'upload (Hors transaction - AVANT)
        UUID uploadedPhotoId = null;
        String uploadedPhotoUri = null;

        if (photoFile != null && !photoFile.isEmpty()) {
            var mediaResult = mediaPort.upload(photoFile, request.getService());
            uploadedPhotoId = mediaResult.id();
            uploadedPhotoUri = mediaResult.uri();
        }

        // 3. Appel de la logique de sauvegarde (Dans une méthode séparée @Transactional)
        User savedUser = saveUserWithData(request, uploadedPhotoId, uploadedPhotoUri);

        return generateTokensForUser(savedUser);
    }

    /* --------------------------- LOGIN --------------------------- */

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Find user by Username OR Email OR Phone
        User user = userRepo.findByUsernameOrEmailOrPhone(
                request.getIdentifier(),
                request.getIdentifier(),
                request.getIdentifier())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!encoder.matches(request.getPassword(), user.getPassword()))
            throw new AuthenticationException("Invalid credentials");

        if (!user.isEnabled()) {
            throw new AuthenticationException("Account is disabled");
        }

        return generateTokensForUser(user);
    }

    /* --------------------------- REFRESH --------------------------- */

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshRepo.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthenticationException("Invalid refresh token"));

        // Sécurité : Détection de réutilisation (Token Theft)
        if (token.isRevoked()) {
            // ALERTE : Quelqu'un essaie d'utiliser un vieux token.
            // On invalide TOUTE la session de l'utilisateur par sécurité.
            refreshRepo.deleteAllByUserId(token.getUserId());
            throw new AuthenticationException("Session compromise (token réutilisé). Veuillez vous reconnecter.");
        }

        if (token.isExpired()) { // Utilisez la méthode utilitaire isExpired() du modèle
            throw new AuthenticationException("Refresh token expired");
        }

        // Rotation
        token.setRevoked(true);
        refreshRepo.save(token);

        User user = userRepo.findById(token.getUserId())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        return generateTokensForUser(user);
    }

    /* --------------------------- LOGOUT --------------------------- */

    public void logout(UUID userId) {
        refreshRepo.deleteAllByUserId(userId);
    }

    /* -------------------------- PROFILE -------------------------- */

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return toUserResponse(user);
    }

    /* -------------------------- HELPER: TOKEN GEN -------------------------- */

    private AuthResponse generateTokensForUser(User user) {
        // Collect Authorities (Roles + Permissions)
        List<String> roleNames = user.getRoles().stream().map(Role::getName).toList();
        List<String> permNames = user.getPermissions().stream().map(Permission::getName).toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roleNames);
        claims.put("permissions", permNames);

        String access = jwt.generateAccessToken(user.getId(), user.getUsername(), claims);
        String refresh = jwt.generateRefreshToken(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token(refresh)
                .userId(user.getId())
                .expiry(Instant.now().plusSeconds(30L * 24 * 3600)) // 30 days
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        refreshRepo.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .user(toUserResponse(user))
                .build();
    }

    private UserResponse toUserResponse(User u) {
        // 1. Récupérer les noms des permissions directes
        Set<String> effectivePermissions = u.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        // 2. Ajouter les permissions provenant des rôles
        if (u.getRoles() != null) {
            u.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(p -> effectivePermissions.add(p.getName()));
                }
            });
        }

        return UserResponse.builder()
                .id(u.getId().toString())
                .username(u.getUsername())
                .email(u.getEmail())
                .phone(u.getPhone())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .service(u.getService())
                .photoId(u.getPhotoId()) 
                .photoUri(u.getPhotoUri())
                .roles(u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .permissions(effectivePermissions) // On renvoie la liste combinée
                .build();
    }
}