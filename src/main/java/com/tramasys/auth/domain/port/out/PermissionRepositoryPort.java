package com.tramasys.auth.domain.port.out;

import com.tramasys.auth.domain.model.Permission;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepositoryPort {
    Permission save(Permission permission);
    Optional<Permission> findById(UUID id);
    Optional<Permission> findByName(String name);
    void deleteById(UUID id);
}
