package com.tramasys.auth.application.service;

import com.tramasys.auth.application.dto.request.RoleCreateRequest;
import com.tramasys.auth.application.dto.response.PermissionResponse;
import com.tramasys.auth.application.dto.response.RoleResponse;
import com.tramasys.auth.domain.exception.DuplicateResourceException;
import com.tramasys.auth.domain.exception.NotFoundException;
import com.tramasys.auth.domain.model.Permission;
import com.tramasys.auth.domain.model.Role;
import com.tramasys.auth.domain.port.out.RoleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {

    private final RoleRepositoryPort roleRepository;

    public RoleService(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getOrCreateRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.of(name)));
    }

    // --- NOUVELLES FONCTIONNALITÉS ---

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RoleResponse createRole(RoleCreateRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Role already exists: " + request.getName());
        }
        Role role = Role.of(request.getName().toUpperCase()); // Force UpperCase convention
        Role saved = roleRepository.save(role);
        return toResponse(saved);
    }

    public void deleteRole(UUID id) {
        // Idéalement vérifier si des users ont ce rôle avant suppression, 
        // mais pour l'instant la cascade BDD gérera le nettoyage relationnel.
        roleRepository.deleteById(id);
    }

    public Set<PermissionResponse> getPermissionsByRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));
        
        return role.getPermissions().stream()
                .map(p -> new PermissionResponse(p.getId().toString(), p.getName(), p.getDescription()))
                .collect(Collectors.toSet());
    }

    private RoleResponse toResponse(Role role) {
        return new RoleResponse(role.getId().toString(), role.getName());
    }
}