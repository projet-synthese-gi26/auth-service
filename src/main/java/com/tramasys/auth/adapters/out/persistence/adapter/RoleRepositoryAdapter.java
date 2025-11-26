package com.tramasys.auth.adapters.out.persistence.adapter;

import com.tramasys.auth.adapters.out.persistence.mapper.DomainEntityMapper;
import com.tramasys.auth.adapters.out.persistence.spring.SpringRoleRepository;
import com.tramasys.auth.adapters.out.persistence.entity.RoleEntity;
import com.tramasys.auth.domain.model.Role;
import com.tramasys.auth.domain.port.out.RoleRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.List; 
import java.util.stream.Collectors;

@Component
@Transactional
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final SpringRoleRepository spring;
    private final DomainEntityMapper mapper;

    public RoleRepositoryAdapter(SpringRoleRepository spring, DomainEntityMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public Role save(Role role) {
        RoleEntity entity = mapper.toEntity(role);
        RoleEntity saved = spring.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return spring.findByName(name).map(mapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return spring.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        spring.deleteById(id);
    }
}
