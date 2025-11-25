package com.tramasys.auth.domain.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    public static Role of(String name) {
        return Role.builder()
                .id(UUID.randomUUID())
                .name(name)
                .build();
    }
}
