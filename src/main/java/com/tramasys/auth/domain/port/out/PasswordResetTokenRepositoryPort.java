package com.tramasys.auth.domain.port.out;

import com.tramasys.auth.domain.model.PasswordResetToken;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepositoryPort {
    PasswordResetToken save(PasswordResetToken token);
    Optional<PasswordResetToken> findByCodeAndUserId(String code, UUID userId);
    void deleteByUserId(UUID userId); // Pour nettoyer les anciens codes
}