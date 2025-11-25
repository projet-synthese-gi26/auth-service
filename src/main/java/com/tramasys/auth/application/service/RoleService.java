package com.tramasys.auth.application.service;

import com.tramasys.auth.domain.model.Role;
import com.tramasys.auth.domain.port.out.RoleRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepositoryPort roleRepository;

    public RoleService(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getOrCreateRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .id(UUID.randomUUID())
                                .name(name)
                                .build()
                ));
    }
}
