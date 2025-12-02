package com.tramasys.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class CorsConfig {

    // Default to * for development, but can be overridden in production
    @Value("${app.cors.allowed-origins:*}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Use setAllowedOriginPatterns instead of setAllowedOrigins to allow wildcards safely
        config.setAllowedOriginPatterns(allowedOrigins); 
        
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        config.setExposedHeaders(List.of("Authorization"));
        
        // IMPORTANT: If your frontend sends "withCredentials: true", this MUST be true.
        // Even with Bearer tokens, it is safer to set this to true for production setups.
        config.setAllowCredentials(true); 
        
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}