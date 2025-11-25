package com.tramasys.auth.application.service;

import com.tramasys.auth.application.dto.request.PermissionCreateRequest;
import com.tramasys.auth.application.dto.request.PermissionUpdateRequest;
import com.tramasys.auth.application.dto.response.PermissionResponse;
import com.tramasys.auth.domain.exception.PermissionException;
import com.tramasys.auth.domain.model.Permission;
import com.tramasys.auth.domain.model.Role;
import com.tramasys.auth.domain.port.out.PermissionRepositoryPort;
import com.tramasys.auth.domain.port.out.RoleRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissionService {

    private final PermissionRepositoryPort permissionRepo;
    private final RoleRepositoryPort roleRepo;

    public PermissionService(PermissionRepositoryPort permissionRepo,
                             RoleRepositoryPort roleRepo) {
        this.permissionRepo = permissionRepo;
        this.roleRepo = roleRepo;
    }

    public PermissionResponse create(PermissionCreateRequest request) {
        permissionRepo.findByName(request.getName())
                .ifPresent(p -> { throw new PermissionException("Permission already exists"); });

        Permission permission = Permission.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Permission saved = permissionRepo.save(permission);

        return PermissionResponse.builder()
                .id(saved.getId().toString())
                .name(saved.getName())
                .description(saved.getDescription())
                .build();
    }

    public PermissionResponse update(UUID id, PermissionUpdateRequest request) {
        Permission permission = permissionRepo.findById(id)
                .orElseThrow(() -> new PermissionException("Not found"));

        permission.setDescription(request.getDescription());
        Permission saved = permissionRepo.save(permission);

        return PermissionResponse.builder()
                .id(saved.getId().toString())
                .name(saved.getName())
                .description(saved.getDescription())
                .build();
    }

    public void delete(UUID id) {
        permissionRepo.deleteById(id);
    }

    public void assignPermissionToRole(String permissionName, String roleName) {
        Permission permission = permissionRepo.findByName(permissionName)
                .orElseThrow(() -> new PermissionException("Permission not found"));

        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new PermissionException("Role not found"));

        role.getPermissions().add(permission);
        roleRepo.save(role);
    }
}
