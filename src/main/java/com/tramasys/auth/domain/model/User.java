package com.tramasys.auth.domain.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private UUID id;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String password;
    
    // New Attribute
    private TramasysService service; 

    private UUID photoId;
    private String photoUri;

    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    public void addRole(Role role) {
        if (role != null)
            roles.add(role);
    }

    public void removeRole(Role role) {
        if (role != null)
            roles.remove(role);
    }

    public boolean hasRole(String roleName) {
        if (roleName == null)
            return false;
        return roles.stream().anyMatch(r -> roleName.equalsIgnoreCase(r.getName()));
    }

    public void addPermission(Permission permission) {
        if (permission != null)
            permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        if (permission != null)
            permissions.remove(permission);
    }

    public boolean hasPermissionExplicit(String permissionName) {
        if (permissionName == null)
            return false;
        return permissions.stream().anyMatch(p -> permissionName.equalsIgnoreCase(p.getName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}