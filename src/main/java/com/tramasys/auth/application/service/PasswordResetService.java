package com.tramasys.auth.application.service;

import com.tramasys.auth.application.dto.request.ForgotPasswordRequest;
import com.tramasys.auth.application.dto.request.ResetPasswordConfirmRequest;
import com.tramasys.auth.application.dto.request.VerifyCodeRequest;
import com.tramasys.auth.domain.exception.InvalidCredentialsException;
import com.tramasys.auth.domain.exception.NotFoundException;
import com.tramasys.auth.domain.model.PasswordResetToken;
import com.tramasys.auth.domain.model.User;
import com.tramasys.auth.domain.port.out.NotificationServicePort;
import com.tramasys.auth.domain.port.out.PasswordResetTokenRepositoryPort;
import com.tramasys.auth.domain.port.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetService {

    private final UserRepositoryPort userRepo;
    private final PasswordResetTokenRepositoryPort tokenRepo;
    private final NotificationServicePort notificationPort;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.otp.validity-minutes:15}")
    private long otpValidityMinutes;

    public PasswordResetService(UserRepositoryPort userRepo,
                                PasswordResetTokenRepositoryPort tokenRepo,
                                NotificationServicePort notificationPort,
                                PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.notificationPort = notificationPort;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Étape 1 : Générer le code et envoyer l'email
     */
    public void initiatePasswordReset(ForgotPasswordRequest request) {
        // 1. Vérifier si l'utilisateur existe
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé avec cet email"));

        // 2. Nettoyer les anciens tokens existants pour cet utilisateur
        tokenRepo.deleteByUserId(user.getId());

        // 3. Générer un code à 6 chiffres
        String code = generateRandomCode();

        // 4. Créer et sauvegarder le token
        PasswordResetToken token = PasswordResetToken.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .code(code)
                .createdAt(Instant.now())
                .expiryDate(Instant.now().plus(otpValidityMinutes, ChronoUnit.MINUTES))
                .build();

        tokenRepo.save(token);

        // 5. Envoyer l'email via l'API de notification
        // Les clés "username" et "code" doivent correspondre aux variables {{username}} et {{code}} dans votre template HTML
        Map<String, String> templateVariables = Map.of(
                "username", user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
                "code", code
        );

        notificationPort.sendPasswordResetEmail(user.getEmail(), templateVariables);
    }

    /**
     * Étape 2 : Vérifier si le code est valide (utilisé par le frontend avant d'afficher le formulaire de MDP)
     */
    public void verifyCode(VerifyCodeRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        validateToken(request.getCode(), user.getId());
    }

    /**
     * Étape 3 : Réinitialiser le mot de passe
     */
    public void resetPassword(ResetPasswordConfirmRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // 1. Vérification finale du token
        validateToken(request.getCode(), user.getId());

        // 2. Mise à jour du mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        // 3. Consommer le token (le supprimer pour qu'il ne soit plus réutilisable)
        tokenRepo.deleteByUserId(user.getId());
    }

    // --- Helpers ---

    private void validateToken(String code, UUID userId) {
        PasswordResetToken token = tokenRepo.findByCodeAndUserId(code, userId)
                .orElseThrow(() -> new InvalidCredentialsException("Code invalide ou expiré"));

        if (token.isExpired()) {
            throw new InvalidCredentialsException("Le code a expiré. Veuillez refaire une demande.");
        }
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000); // 0 à 999999
        return String.format("%06d", num); // Pad avec des zéros à gauche : "004812"
    }
}