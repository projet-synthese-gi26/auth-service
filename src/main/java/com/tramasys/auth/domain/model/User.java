package com.tramasys.auth.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String password;

    private boolean enabled = true;

    private Instant createdAt = Instant.now();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    // getters/setters by Lombok

    /* -------------------------
       Méthodes utilitaires domaine
       ------------------------- */

    public void addRole(Role role) {
        if (role != null) roles.add(role);
    }

    public void removeRole(Role role) {
        if (role != null) roles.remove(role);
    }

    public boolean hasRole(String roleName) {
        if (roleName == null) return false;
        return roles.stream().anyMatch(r -> roleName.equalsIgnoreCase(r.getName()));
    }

    public void addPermission(Permission permission) {
        if (permission != null) permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        if (permission != null) permissions.remove(permission);
    }

    /**
     * Vérifie si l'utilisateur possède une permission effective :
     * - soit dans ses permissions explicites,
     * - soit via un rôle (cela suppose que les rôles ont aussi des permissions dans l'implémentation).
     *
     * Note : La logique exacte de résolution role->permissions appartiendra au service / adapter
     * qui calculera l'union roles+user permissions. Ici on fait la vérification côté user pour
     * permissions explicites uniquement.
     */
    public boolean hasPermissionExplicit(String permissionName) {
        if (permissionName == null) return false;
        return permissions.stream().anyMatch(p -> permissionName.equalsIgnoreCase(p.getName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
