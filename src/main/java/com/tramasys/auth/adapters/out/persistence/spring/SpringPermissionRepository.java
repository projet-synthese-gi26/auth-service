package com.tramasys.auth.adapters.out.persistence.spring;

import com.tramasys.auth.adapters.out.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringPermissionRepository extends JpaRepository<PermissionEntity, UUID> {
    Optional<PermissionEntity> findByName(String name);
}
