package com.tramasys.auth.domain.port.in;

import com.tramasys.auth.domain.model.Permission;
import com.tramasys.auth.domain.model.Role;
import com.tramasys.auth.domain.model.User;

import java.util.UUID;

/**
 * Cas d'usage pour la gestion des permissions (création, assignation, suppression).
 */
public interface PermissionUseCase {
    Permission createPermission(Permission permission);
    Permission updatePermission(UUID permissionId, Permission permission);
    void deletePermission(UUID permissionId);

    Role assignPermissionToRole(UUID roleId, UUID permissionId);
    Role removePermissionFromRole(UUID roleId, UUID permissionId);

    User addPermissionToUser(UUID userId, UUID permissionId);
    User removePermissionFromUser(UUID userId, UUID permissionId);
}
