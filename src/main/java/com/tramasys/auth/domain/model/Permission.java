package com.tramasys.auth.domain.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id @GeneratedValue
    private UUID id;

    @Column(unique=true, nullable=false)
    private String name; // service:object:action

    private String description;
    // getters/setters

    public static Permission of(String name, String description) {
        return Permission.builder()
                .id(UUID.randomUUID())
                .name(name)
                .description(description)
                .build();
    }
}