package com.tramasys.auth.domain.port.out;

import com.tramasys.auth.domain.model.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByUserId(UUID userId);
    void deleteById(UUID id);
    void deleteAllByUserId(UUID userId);
}
