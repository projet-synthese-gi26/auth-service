package com.tramasys.auth.adapters.in.web;

import com.tramasys.auth.application.dto.request.PermissionCreateRequest;
import com.tramasys.auth.application.dto.request.PermissionUpdateRequest;
import com.tramasys.auth.application.dto.request.AssignPermissionToRoleRequest;
import com.tramasys.auth.application.dto.response.PermissionResponse;
import com.tramasys.auth.application.service.PermissionService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permissions", description = "Permission management")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public PermissionResponse create(@RequestBody PermissionCreateRequest request) {
        return permissionService.create(request);
    }

    @PutMapping("/{id}")
    public PermissionResponse update(
            @PathVariable UUID id,
            @RequestBody PermissionUpdateRequest request) {
        return permissionService.update(id, request);
    }

    @GetMapping
    @Operation(summary = "List all Permissions")
    @SecurityRequirements()
    public List<PermissionResponse> getAll() {
        return permissionService.getAllPermissions();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        permissionService.delete(id);
    }

    @PostMapping("/assign/{roleName}")
    public void assignToRole(
            @PathVariable String roleName,
            @RequestBody AssignPermissionToRoleRequest request) {
        permissionService.assignPermissionToRole(request.getPermissionName(), roleName);
    }
}
