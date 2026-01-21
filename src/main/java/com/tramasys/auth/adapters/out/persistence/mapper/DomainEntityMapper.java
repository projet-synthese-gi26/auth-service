package com.tramasys.auth.adapters.out.persistence.mapper;

import com.tramasys.auth.adapters.out.persistence.entity.PermissionEntity;
import com.tramasys.auth.adapters.out.persistence.entity.RefreshTokenEntity;
import com.tramasys.auth.adapters.out.persistence.entity.RoleEntity;
import com.tramasys.auth.adapters.out.persistence.entity.UserEntity;
import com.tramasys.auth.domain.model.Permission;
import com.tramasys.auth.domain.model.RefreshToken;
import com.tramasys.auth.domain.model.Role;
import com.tramasys.auth.domain.model.User;
import com.tramasys.auth.domain.model.PasswordResetToken;
import com.tramasys.auth.adapters.out.persistence.entity.PasswordResetTokenEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper simple entre domaine.model <-> entity.*
 * On garde la logique centralisée ici. Tu peux remplacer par MapStruct si tu préfères.
 */
@Component
public class DomainEntityMapper {

    /* ------------------ Role ------------------ */
    public Role toDomain(RoleEntity e) {
        if (e == null) return null;
        return Role.builder()
                .id(e.getId())
                .name(e.getName())
                .permissions(e.getPermissions() == null ?
                    Set.of() :
                    e.getPermissions().stream().map(this::toDomain).collect(Collectors.toSet())
                )
                .build();
    }

    public RoleEntity toEntity(Role r) {
        if (r == null) return null;
        RoleEntity entity = RoleEntity.builder()
        .id(r.getId() == null ? UUID.randomUUID() : r.getId())
        .name(r.getName())
        .build();

        if (r.getPermissions() != null) {
            entity.setPermissions(
                    r.getPermissions().stream().map(this::toEntity).collect(Collectors.toSet())
            );
        }

        return entity;
    }

    /* ------------------ Permission ------------------ */
    public Permission toDomain(PermissionEntity e) {
        if (e == null) return null;
        return Permission.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .build();
    }

    public PermissionEntity toEntity(Permission p) {
        if (p == null) return null;
        return PermissionEntity.builder()
                .id(p.getId() == null ? UUID.randomUUID() : p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .build();
    }

    /* ------------------ User ------------------ */
    public User toDomain(UserEntity e) {
        if (e == null) return null;
        Set<Role> roles = e.getRoles() == null ? Set.of() : e.getRoles().stream().map(this::toDomain).collect(Collectors.toSet());
        Set<Permission> perms = e.getPermissions() == null ? Set.of() : e.getPermissions().stream().map(this::toDomain).collect(Collectors.toSet());
        return User.builder()
                .id(e.getId())
                .username(e.getUsername())
                .email(e.getEmail())
                .phone(e.getPhone())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .password(e.getPasswordHash())
                .service(e.getService())
                .photoId(e.getPhotoId())
                .photoUri(e.getPhotoUri())
                .enabled(e.isEnabled())
                .createdAt(e.getCreatedAt() == null ? Instant.now() : e.getCreatedAt())
                .roles(roles)
                .permissions(perms)
                .build();
    }

    public UserEntity toEntity(User u) {
        if (u == null) return null;
        UserEntity.UserEntityBuilder builder = UserEntity.builder()
                .id(u.getId() == null ? UUID.randomUUID() : u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .phone(u.getPhone())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .passwordHash(u.getPassword())
                .service(u.getService())
                .photoId(u.getPhotoId())
                .photoUri(u.getPhotoUri())
                .enabled(u.isEnabled())
                .createdAt(u.getCreatedAt() == null ? Instant.now() : u.getCreatedAt());

        if (u.getRoles() != null) {
            builder.roles(u.getRoles().stream().map(this::toEntity).collect(Collectors.toSet()));
        }
        if (u.getPermissions() != null) {
            builder.permissions(u.getPermissions().stream().map(this::toEntity).collect(Collectors.toSet()));
        }
        return builder.build();
    }

    /* ------------------ RefreshToken ------------------ */
    public RefreshToken toDomain(RefreshTokenEntity e) {
        if (e == null) return null;
        return RefreshToken.builder()
                .id(e.getId())
                .token(e.getToken())
                .userId(e.getUser() == null ? null : e.getUser().getId())
                .expiry(e.getExpiry())
                .revoked(e.isRevoked())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public RefreshTokenEntity toEntity(RefreshToken r, UserEntity userEntity) {
        if (r == null) return null;
        return RefreshTokenEntity.builder()
                .id(r.getId() == null ? UUID.randomUUID() : r.getId())
                .token(r.getToken())
                .user(userEntity)
                .expiry(r.getExpiry())
                .revoked(r.isRevoked())
                .createdAt(r.getCreatedAt() == null ? Instant.now() : r.getCreatedAt())
                .build();
    }

    public PasswordResetToken toDomain(PasswordResetTokenEntity e) {
        if (e == null) return null;
        return PasswordResetToken.builder()
                .id(e.getId())
                .code(e.getCode())
                // On extrait juste l'ID du user pour le domaine
                .userId(e.getUser() != null ? e.getUser().getId() : null)
                .expiryDate(e.getExpiryDate())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public PasswordResetTokenEntity toEntity(PasswordResetToken d, UserEntity userEntity) {
        if (d == null) return null;
        return PasswordResetTokenEntity.builder()
                .id(d.getId() == null ? UUID.randomUUID() : d.getId())
                .code(d.getCode())
                .user(userEntity) // On a besoin de l'entité User complète ici
                .expiryDate(d.getExpiryDate())
                .createdAt(d.getCreatedAt() == null ? Instant.now() : d.getCreatedAt())
                .build();
    }
}
