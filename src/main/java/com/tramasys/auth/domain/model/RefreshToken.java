package com.tramasys.auth.domain.model;

import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    private UUID id;
    private String token;
    private UUID userId;
    private Instant expiry;
    private boolean revoked = false;
    @Builder.Default
    private Instant createdAt = Instant.now();

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