package com.tramasys.auth.adapters.in.web;

import com.tramasys.auth.application.dto.response.RoleResponse;
import com.tramasys.auth.application.service.RoleService;
import com.tramasys.auth.domain.model.Role;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roles;

    public RoleController(RoleService roles) {
        this.roles = roles;
    }

    @GetMapping("/{name}")
    public RoleResponse getRole(@PathVariable String name) {
        Role role = roles.getOrCreateRole(name);
        return new RoleResponse(role.getId().toString(), role.getName());
    }
}
