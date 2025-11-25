package com.tramasys.auth.adapters.in.web;

import com.tramasys.auth.application.dto.request.PermissionCreateRequest;
import com.tramasys.auth.application.dto.request.PermissionUpdateRequest;
import com.tramasys.auth.application.dto.request.AssignPermissionToRoleRequest;
import com.tramasys.auth.application.dto.response.PermissionResponse;
import com.tramasys.auth.application.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
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
