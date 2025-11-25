package com.tramasys.auth.util;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Change 1: Extend OncePerRequestFilter (Spring Standard) instead of raw Filter
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;

    public JwtAuthenticationFilter(JwtUtil jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 1. Check if token exists
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            UserContext.clear();
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 2. Validate Token
            Claims claims = jwt.validateToken(token);

            UUID userId = UUID.fromString(claims.getSubject());
            String username = claims.get("username", String.class);
            List<String> roles = (List<String>) claims.get("roles");
            List<String> permissions = (List<String>) claims.get("permissions");

            // 3. Set your Custom Context (for your business logic)
            UserContext.set(userId, username, roles, permissions);

            // 4. CRITICAL FIX: Inform Spring Security that the user is authenticated
            // We convert your Roles into Spring Authorities (SimpleGrantedAuthority)
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Convention: Add ROLE_ prefix
                    .collect(Collectors.toList());

            // Create standard Spring Authentication object
            UsernamePasswordAuthenticationToken springAuth = new UsernamePasswordAuthenticationToken(userId, null,
                    authorities);

            // Set it in the context
            SecurityContextHolder.getContext().setAuthentication(springAuth);

        } catch (Exception e) {
            // Token invalid or expired
            UserContext.clear();
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}