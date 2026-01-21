package com.tramasys.auth.adapters.in.web;

import com.tramasys.auth.application.dto.request.ForgotPasswordRequest;
import com.tramasys.auth.application.dto.request.ResetPasswordConfirmRequest;
import com.tramasys.auth.application.dto.request.VerifyCodeRequest;
import com.tramasys.auth.application.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@Tag(name = "Password Management", description = "Endpoints publics pour la gestion du mot de passe oublié")
public class PasswordController {

    private final PasswordResetService passwordResetService;

    public PasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // Route 1 : Demande de réinitialisation (Public)
    @Operation(summary = "Forgot Password", description = "Génère un code OTP et l'envoie par email.")
    @PostMapping("/forgot")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204: On ne dit pas explicitement si l'email existe ou non pour éviter l'énumération (sécurité)
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.initiatePasswordReset(request);
    }

    // Route 2 : Vérification du code (Public)
    @Operation(summary = "Verify OTP Code", description = "Vérifie si le code est valide avant d'afficher le formulaire de changement de mot de passe.")
    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public void verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        passwordResetService.verifyCode(request);
    }

    // Route 3 : Réinitialisation finale (Public)
    @Operation(summary = "Reset Password", description = "Change le mot de passe en utilisant le code OTP validé.")
    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordConfirmRequest request) {
        passwordResetService.resetPassword(request);
    }
}