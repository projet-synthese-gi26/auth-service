package com.tramasys.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenValidityMillis;
    private final long refreshTokenValidityMillis;
    private final String issuer;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-minutes}") long accessTokenMinutes,
            @Value("${jwt.refresh-token-days}") long refreshTokenDays,
            @Value("${jwt.issuer}") String issuer
    ) {
        // We use a safe decoder in case you use a Base64 string in properties
        // If your secret is plain text, use: this.key = Keys.hmacShaKeyFor(secret.getBytes());
        // But standard practice is often Base64 encoded secrets for 256-bit keys.
        // For simplicity with your current setup, let's treat it as bytes, 
        // but ensure it's long enough.
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
             throw new IllegalArgumentException("JWT Secret must be at least 32 characters long for HS256");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        
        this.accessTokenValidityMillis = accessTokenMinutes * 60 * 1000;
        this.refreshTokenValidityMillis = refreshTokenDays * 24 * 60 * 60 * 1000;
        this.issuer = issuer;
    }

    public String generateAccessToken(UUID userId, String username, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(userId.toString())
                .setIssuer(issuer)
                .claim("username", username)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(accessTokenValidityMillis)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(userId.toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(refreshTokenValidityMillis)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT claims string is empty");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT signature");
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(validateToken(token).getSubject());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = validateToken(token);
        return claimsResolver.apply(claims);
    }
}