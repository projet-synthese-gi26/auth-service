package com.tramasys.auth.adapters.out.persistence.spring;

import com.tramasys.auth.adapters.out.persistence.entity.PasswordResetTokenEntity;
import com.tramasys.auth.adapters.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {
    // Trouve un token par le code ET l'utilisateur (double vérification)
    Optional<PasswordResetTokenEntity> findByCodeAndUser(String code, UserEntity user);

    // Supprime tous les tokens d'un utilisateur (nettoyage)
    void deleteAllByUser(UserEntity user);
}