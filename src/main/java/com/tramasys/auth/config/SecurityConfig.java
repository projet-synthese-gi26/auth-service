package com.tramasys.auth.config;

import com.tramasys.auth.util.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
            CorsConfigurationSource corsConfigurationSource,
            JwtAuthenticationEntryPoint unauthorizedHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler))
                .authorizeHttpRequests(auth -> auth
                        // 1. Allow Public Auth Endpoints
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh", "/error")
                        .permitAll()

                        // 2. Allow Swagger UI Resources
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**")
                        .permitAll()

                        // 3. CRITICAL FIX: Allow all OPTIONS requests (Pre-flight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // --- AJOUT : Routes publiques GET pour Rôles et Permissions ---
                        .requestMatchers(HttpMethod.GET, "/api/roles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/permissions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/roles/*/permissions").permitAll()
                        // User listing endpoint
                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/service/**").permitAll() // Get by Service
                        .requestMatchers(HttpMethod.GET, "/api/users/email/**").permitAll() // Get by Email
                        .requestMatchers(HttpMethod.GET, "/api/users/username/**").permitAll() // Get by Username
                        .requestMatchers(HttpMethod.GET, "/api/users/phone/**").permitAll()
                        // --------------------------------------------------------------

                        // 4. Everything else requires authentication
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Expose AuthenticationManager if you need it later (standard practice)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
