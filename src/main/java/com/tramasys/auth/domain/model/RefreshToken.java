package com.tramasys.auth.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id @GeneratedValue
    private UUID id;

    @Column(unique=true, nullable=false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UUID userId;

    private Instant expiry;
    private boolean revoked = false;
    private Instant createdAt = Instant.now();

    // getters/setters by Lombok

    public static RefreshToken createForUser(UUID userId, String token, Instant expiry) {
        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .token(token)
                .expiry(expiry)
                .revoked(false)
                .createdAt(Instant.now())
                .build();
    }

    public boolean isExpired() {
        return expiry != null && expiry.isBefore(Instant.now());
    }
}
