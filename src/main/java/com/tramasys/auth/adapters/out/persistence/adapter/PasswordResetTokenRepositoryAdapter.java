package com.tramasys.auth.adapters.out.persistence.adapter;

import com.tramasys.auth.adapters.out.persistence.entity.PasswordResetTokenEntity;
import com.tramasys.auth.adapters.out.persistence.entity.UserEntity;
import com.tramasys.auth.adapters.out.persistence.mapper.DomainEntityMapper;
import com.tramasys.auth.adapters.out.persistence.spring.SpringPasswordResetTokenRepository;
import com.tramasys.auth.adapters.out.persistence.spring.SpringUserRepository;
import com.tramasys.auth.domain.model.PasswordResetToken;
import com.tramasys.auth.domain.port.out.PasswordResetTokenRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepositoryPort {

    private final SpringPasswordResetTokenRepository springTokenRepo;
    private final SpringUserRepository springUserRepo;
    private final DomainEntityMapper mapper;

    public PasswordResetTokenRepositoryAdapter(SpringPasswordResetTokenRepository springTokenRepo,
            SpringUserRepository springUserRepo,
            DomainEntityMapper mapper) {
        this.springTokenRepo = springTokenRepo;
        this.springUserRepo = springUserRepo;
        this.mapper = mapper;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        UserEntity userEntity = springUserRepo.findById(token.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + token.getUserId()));

        PasswordResetTokenEntity entity = mapper.toEntity(token, userEntity);
        PasswordResetTokenEntity saved = springTokenRepo.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PasswordResetToken> findByCodeAndUserId(String code, UUID userId) {
        // On récupère d'abord le user proxy/entity
        UserEntity userEntity = springUserRepo.getReferenceById(userId);
        // Note: getReferenceById est lazy, utile si on veut juste passer la référence
        // Mais pour plus de sûreté on peut utiliser findById :
        // return springUserRepo.findById(userId)
        // .flatMap(u -> springTokenRepo.findByCodeAndUser(code, u))
        // .map(mapper::toDomain);

        // Approche simple:
        Optional<UserEntity> userOpt = springUserRepo.findById(userId);
        if (userOpt.isEmpty())
            return Optional.empty();

        return springTokenRepo.findByCodeAndUser(code, userOpt.get())
                .map(mapper::toDomain);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        springUserRepo.findById(userId).ifPresent(springTokenRepo::deleteAllByUser);
    }
}