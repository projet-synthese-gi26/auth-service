package com.tramasys.auth.domain.model;

import java.util.UUID;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    private UUID id;
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