package com.tramasys.auth.adapters.in.web;

import com.tramasys.auth.application.dto.request.RoleCreateRequest;
import com.tramasys.auth.application.dto.response.PermissionResponse;
import com.tramasys.auth.application.dto.response.RoleResponse;
import com.tramasys.auth.application.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles") // Normalisation du chemin
@Tag(name = "Roles", description = "Role Management (RBAC)")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(summary = "List all Roles")
    public List<RoleResponse> getAll() {
        return roleService.getAllRoles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new Role")
    public RoleResponse create(@RequestBody RoleCreateRequest request) {
        return roleService.createRole(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Role")
    public void delete(@PathVariable UUID id) {
        roleService.deleteRole(id);
    }

    @GetMapping("/{name}/permissions")
    @Operation(summary = "Get Permissions for a specific Role")
    public Set<PermissionResponse> getRolePermissions(@PathVariable String name) {
        return roleService.getPermissionsByRole(name);
    }
}