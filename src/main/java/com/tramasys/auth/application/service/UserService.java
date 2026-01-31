package com.tramasys.auth.application.service;

import com.tramasys.auth.application.dto.request.ChangePasswordRequest;
import com.tramasys.auth.application.dto.request.UserUpdateRequest;
import com.tramasys.auth.application.dto.response.UserResponse;
import com.tramasys.auth.domain.exception.DuplicateResourceException;
import com.tramasys.auth.domain.exception.InvalidCredentialsException;
import com.tramasys.auth.domain.exception.NotFoundException;
import com.tramasys.auth.domain.model.Permission;
import com.tramasys.auth.domain.model.Role;
import com.tramasys.auth.domain.model.User;
import com.tramasys.auth.domain.port.out.RoleRepositoryPort;
import com.tramasys.auth.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tramasys.auth.domain.port.out.MediaStoragePort;
import org.springframework.web.multipart.MultipartFile;
import com.tramasys.auth.domain.port.out.RefreshTokenRepositoryPort;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.tramasys.auth.domain.model.TramasysService;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepositoryPort userRepo;
    private final RoleRepositoryPort roleRepo; // Ajout
    private final PasswordEncoder encoder;     
    private final MediaStoragePort mediaPort;
    private final RefreshTokenRepositoryPort refreshRepo;

    public UserService(UserRepositoryPort userRepo, RoleRepositoryPort roleRepo, PasswordEncoder encoder, MediaStoragePort mediaPort, RefreshTokenRepositoryPort refreshRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.mediaPort = mediaPort;
        this.refreshRepo = refreshRepo;
    }

    public UserResponse updateProfilePicture(UUID userId, MultipartFile file) {
        User user = findUserById(userId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        if (user.getPhotoId() != null) {
            // UPDATE: Remplacement via l'API Media
            var result = mediaPort.replace(user.getPhotoId(), file);
            // L'ID ne change généralement pas, mais l'URI pourrait changer (versionning, cache busting)
            user.setPhotoUri(result.uri());
        } else {
            // CREATE: Upload initial
            var result = mediaPort.upload(file, user.getService());
            user.setPhotoId(result.id());
            user.setPhotoUri(result.uri());
        }

        User saved = userRepo.save(user);
        return toUserResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) { return toUserResponse(findUserById(id)); }
    
    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        return toUserResponse(userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Transactional(readOnly = true)
    public UserResponse getByUsername(String username) {
        return toUserResponse(userRepo.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found")));
    }
    
    @Transactional(readOnly = true)
    public UserResponse getByPhone(String phone) {
        return toUserResponse(userRepo.findByPhone(phone).orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllByService(TramasysService service) {
        return userRepo.findAllByService(service).stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse update(UUID userId, UserUpdateRequest request) {
        User user = findUserById(userId);
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepo.existsByPhone(request.getPhone())) {
                throw new DuplicateResourceException("Phone number already used");
            }
            user.setPhone(request.getPhone());
        }
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        return toUserResponse(userRepo.save(user));
    }

    // --- NOUVELLES FONCTIONNALITÉS ---

    public void addRoleToUser(UUID userId, String roleName) {
        User user = findUserById(userId);
        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));
        
        user.addRole(role);
        userRepo.save(user);
    }

    public void removeRoleFromUser(UUID userId, String roleName) {
        User user = findUserById(userId);
        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));
        
        user.removeRole(role);
        userRepo.save(user);
    }

    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = findUserById(userId);

        // 1. Vérifier l'ancien mot de passe
        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("L'ancien mot de passe est incorrect");
        }

        // 2. Mettre à jour avec le nouveau
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepo.save(user);
    }

    // --- HELPERS ---

    private User findUserById(UUID id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
    }
    

    private UserResponse toUserResponse(User u) {
        // Logique corrigée pour inclure les permissions héritées
        Set<String> effectivePermissions = u.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

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
                .service(u.getService())
                .email(u.getEmail())
                .phone(u.getPhone())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .roles(u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .permissions(effectivePermissions)
                .photoId(u.getPhotoId())
                .photoUri(u.getPhotoUri())
                .build();
    }

    public void deleteUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        
        // 1. Suppression explicite des tokens (sécurité + intégrité référentielle)
        refreshRepo.deleteAllByUserId(user.getId());
        
        // 2. Suppression de l'utilisateur (la BDD gérera la cascade pour les rôles/permissions via les tables de jointure)
        userRepo.deleteByEmail(email);
        
        // Optionnel : Si vous voulez supprimer la photo du Media Service
        if (user.getPhotoId() != null) {
            try {
                mediaPort.delete(user.getPhotoId());
            } catch (Exception e) {
                // On log juste, on ne bloque pas la suppression de l'user pour ça
                System.err.println("Warning: Failed to delete media for user " + email);
            }
        }
    }
}