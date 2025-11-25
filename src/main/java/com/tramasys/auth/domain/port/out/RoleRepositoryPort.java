package com.tramasys.auth.domain.port.out;

import com.tramasys.auth.domain.model.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepositoryPort {
    Role save(Role role);
    Optional<Role> findById(UUID id);
    Optional<Role> findByName(String name);
}