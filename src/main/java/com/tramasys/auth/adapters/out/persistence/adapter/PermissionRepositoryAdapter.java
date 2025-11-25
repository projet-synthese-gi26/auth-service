package com.tramasys.auth.adapters.out.persistence.adapter;

import com.tramasys.auth.adapters.out.persistence.mapper.DomainEntityMapper;
import com.tramasys.auth.adapters.out.persistence.spring.SpringPermissionRepository;
import com.tramasys.auth.adapters.out.persistence.entity.PermissionEntity;
import com.tramasys.auth.domain.model.Permission;
import com.tramasys.auth.domain.port.out.PermissionRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final SpringPermissionRepository spring;
    private final DomainEntityMapper mapper;

    public PermissionRepositoryAdapter(SpringPermissionRepository spring, DomainEntityMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = mapper.toEntity(permission);
        PermissionEntity saved = spring.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return spring.findByName(name).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        spring.deleteById(id);
    }
}
