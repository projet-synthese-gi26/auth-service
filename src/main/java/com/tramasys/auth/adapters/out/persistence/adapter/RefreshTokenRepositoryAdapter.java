package com.tramasys.auth.adapters.out.persistence.adapter;

import com.tramasys.auth.adapters.out.persistence.entity.RefreshTokenEntity;
import com.tramasys.auth.adapters.out.persistence.entity.UserEntity;
import com.tramasys.auth.adapters.out.persistence.mapper.DomainEntityMapper;
import com.tramasys.auth.adapters.out.persistence.spring.SpringRefreshTokenRepository;
import com.tramasys.auth.adapters.out.persistence.spring.SpringUserRepository;
import com.tramasys.auth.domain.model.RefreshToken;
import com.tramasys.auth.domain.port.out.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Transactional
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final SpringRefreshTokenRepository spring;
    private final SpringUserRepository springUserRepository;
    private final DomainEntityMapper mapper;

    public RefreshTokenRepositoryAdapter(SpringRefreshTokenRepository spring,
                                         SpringUserRepository springUserRepository,
                                         DomainEntityMapper mapper) {
        this.spring = spring;
        this.springUserRepository = springUserRepository;
        this.mapper = mapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        // need userEntity
        UserEntity userEntity = springUserRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + refreshToken.getUserId()));
        RefreshTokenEntity entity = mapper.toEntity(refreshToken, userEntity);
        RefreshTokenEntity saved = spring.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return spring.findByToken(token).map(mapper::toDomain);
    }

    @Override
    public List<RefreshToken> findAllByUserId(UUID userId) {
        UserEntity userEntity = springUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return spring.findAllByUser(userEntity).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        spring.deleteById(id);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        UserEntity userEntity = springUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        spring.deleteAllByUser(userEntity);
    }
}
