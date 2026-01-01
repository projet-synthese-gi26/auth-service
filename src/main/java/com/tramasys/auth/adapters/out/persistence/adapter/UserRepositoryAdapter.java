package com.tramasys.auth.adapters.out.persistence.adapter;

import com.tramasys.auth.adapters.out.persistence.mapper.DomainEntityMapper;
import com.tramasys.auth.adapters.out.persistence.spring.SpringUserRepository;
import com.tramasys.auth.adapters.out.persistence.entity.UserEntity;
import com.tramasys.auth.domain.model.User;
import com.tramasys.auth.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tramasys.auth.domain.model.TramasysService;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringUserRepository spring;
    private final DomainEntityMapper mapper;

    public UserRepositoryAdapter(SpringUserRepository spring, DomainEntityMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = spring.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return spring.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return spring.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return spring.findByPhone(phone).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmailOrPhone(String username, String email, String phone) {
        return spring.findByUsernameOrEmailOrPhone(username, email, phone).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return spring.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return spring.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return spring.existsByPhone(phone);
    }

    @Override
    public List<User> findAllByService(TramasysService service) {
        return spring.findAllByService(service).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return spring.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
