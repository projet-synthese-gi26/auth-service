package com.tramasys.auth.domain.port.out;

import com.tramasys.auth.domain.model.Permission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepositoryPort {
    Permission save(Permission permission);

    Optional<Permission> findById(UUID id);

    Optional<Permission> findByName(String name);

    List<Permission> findAll(); // <--- AJOUT

    void deleteById(UUID id);
}