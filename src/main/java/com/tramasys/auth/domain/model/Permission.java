package com.tramasys.auth.domain.model;

import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    private UUID id;
    private String name; // service:object:action
    private String description;

    public static Permission of(String name, String description) {
        return Permission.builder()
                .id(UUID.randomUUID())
                .name(name)
                .description(description)
                .build();
    }
}