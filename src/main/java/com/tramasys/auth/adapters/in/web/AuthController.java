package com.tramasys.auth.adapters.in.web;

import com.tramasys.auth.application.dto.request.*;
import com.tramasys.auth.application.dto.response.*;
import com.tramasys.auth.application.service.AuthService;
import com.tramasys.auth.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter; // <--- IMPORT AJOUTÉ
import io.swagger.v3.oas.annotations.media.Content; // <--- IMPORT AJOUTÉ
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // <--- IMPORT AJOUTÉ
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import com.tramasys.auth.domain.exception.AuthenticationException;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Operations related to User Auth (Login, Register, Refresh)")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account with optional profile picture.")
    @ApiResponse(responseCode = "201", description = "User successfully registered")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "409", description = "Username or Email already exists")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(
            @Valid @RequestPart("data") @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) 
            RegisterRequest request,

            @RequestPart(value = "file", required = false) MultipartFile file) {
        return auth.register(request, file);
    }

    @Operation(summary = "User Login", description = "Authenticates a user via Username/Email/Phone and Password.")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        // ideally add @Valid to LoginRequest as well in the future
        return auth.login(request);
    }

    @Operation(summary = "Refresh Access Token", description = "Uses a valid Refresh Token to generate a new Access Token.")
    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest request) {
        return auth.refreshToken(request);
    }

    @Operation(summary = "Logout", description = "Revokes the refresh token for the user.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns 204 No Content
    public void logout(@PathVariable UUID userId) {
        auth.logout(userId);
    }

    @Operation(summary = "Get Current Profile", description = "Returns the profile of the currently authenticated user.")
    @SecurityRequirement(name = "bearerAuth") // Tells Swagger this endpoint needs the Lock icon
    @GetMapping("/me")
    public UserResponse me() {
        UUID userId = UserContext.getUserId();

        if (userId == null) {
            throw new AuthenticationException("User context is empty. Ensure token is valid.");
        }

        // Fetch fresh profile from DB to ensure data consistency
        return auth.getUserProfile(userId);
    }
}