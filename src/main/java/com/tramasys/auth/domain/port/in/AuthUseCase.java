package com.tramasys.auth.domain.port.in;

import com.tramasys.auth.domain.model.RefreshToken;
import com.tramasys.auth.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port IN (use-cases) pour l'authentification.
 * Les DTOs d'entrée/sortie (RegisterRequest, LoginResponse, ...) seront dans la couche application.
 */
public interface AuthUseCase {

    /**
     * Inscrire un nouvel utilisateur (retourne l'utilisateur créé).
     * Validation/Hash du mot de passe à effectuer dans l'implémentation.
     */
    User register(User user, String rawPassword);

    /**
     * Authentifier par identifiant (username/email/phone) et mot de passe brut.
     * Retourne l'utilisateur si ok.
     */
    User authenticate(String identifier, String rawPassword);

    /**
     * Créer et persister un refresh token pour un userId.
     */
    RefreshToken createRefreshToken(UUID userId);

    /**
     * Valider un refresh token string et renvoyer l'objet lié (ou lancer exception).
     */
    RefreshToken validateRefreshToken(String token);

    /**
     * Révoquer un refresh token (logout).
     */
    void revokeRefreshToken(String token);
}
