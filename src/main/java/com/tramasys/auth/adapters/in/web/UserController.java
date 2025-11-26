package com.tramasys.auth.adapters.in.web;

import com.tramasys.auth.application.dto.request.UserUpdateRequest;
import com.tramasys.auth.application.dto.response.UserResponse;
import com.tramasys.auth.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints (Search, Update)")
@SecurityRequirement(name = "bearerAuth") // Sécurise toutes les routes de ce contrôleur
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User by ID")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get User by Email")
    public UserResponse getByEmail(@PathVariable String email) {
        return userService.getByEmail(email);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get User by Username")
    public UserResponse getByUsername(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "Get User by Phone")
    public UserResponse getByPhone(@PathVariable String phone) {
        return userService.getByPhone(phone);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update User Profile", description = "Updates first name, last name, or phone.")
    public UserResponse update(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @PostMapping("/{id}/roles/{roleName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Add a Role to User", description = "Assigns an existing role to the user.")
    public void addRole(@PathVariable UUID id, @PathVariable String roleName) {
        userService.addRoleToUser(id, roleName);
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a Role from User")
    public void removeRole(@PathVariable UUID id, @PathVariable String roleName) {
        userService.removeRoleFromUser(id, roleName);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Change Password", description = "Updates the user's password. Requires old password check.")
    public void changePassword(@PathVariable UUID id,
            @RequestBody com.tramasys.auth.application.dto.request.ChangePasswordRequest request) {
        userService.changePassword(id, request);
    }
}