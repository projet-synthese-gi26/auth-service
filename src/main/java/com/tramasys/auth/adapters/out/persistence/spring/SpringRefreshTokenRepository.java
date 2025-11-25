package com.tramasys.auth.adapters.out.persistence.spring;

import com.tramasys.auth.adapters.out.persistence.entity.RefreshTokenEntity;
import com.tramasys.auth.adapters.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByToken(String token);
    List<RefreshTokenEntity> findAllByUser(UserEntity user);
    void deleteAllByUser(UserEntity user);
}
