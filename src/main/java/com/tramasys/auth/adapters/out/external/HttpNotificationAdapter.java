package com.tramasys.auth.adapters.out.external;

import com.tramasys.auth.domain.exception.DomainException;
import com.tramasys.auth.domain.port.out.NotificationServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class HttpNotificationAdapter implements NotificationServicePort {

    private static final Logger log = LoggerFactory.getLogger(HttpNotificationAdapter.class);
    private final RestClient restClient;
    
    @Value("${notification.service.token}")
    private String serviceToken;

    @Value("${notification.template.id.reset-password}")
    private int resetPasswordTemplateId;

    public HttpNotificationAdapter(@Value("${notification.service.url}") String notificationServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(notificationServiceUrl)
                .build();
    }

    // On utilise @Async (optionnel) pour ne pas bloquer l'utilisateur si l'envoi d'email prend du temps
    // Assurez-vous d'avoir @EnableAsync dans votre configuration principale si vous voulez l'utiliser
    @Async 
    @Override
    public void sendPasswordResetEmail(String email, Map<String, String> variables) {
        try {
            // DTO correspondant à NotificationSendRequest de votre API Notification
            var request = new NotificationRequest(
                    "EMAIL",
                    resetPasswordTemplateId,
                    List.of(email),
                    variables
            );

            log.info("Sending password reset email to: {}", email);

            restClient.post()
                    .uri("/api/v1/notifications/send")
                    .header("X-Service-Token", serviceToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Password reset email sent successfully.");

        } catch (Exception e) {
            // On log l'erreur mais on ne crash pas forcément tout le processus métier ici,
            // ou alors on lance une DomainException si c'est critique.
            log.error("Failed to send notification via Notification Service: {}", e.getMessage());
            throw new DomainException("Impossible d'envoyer l'email de notification", e);
        }
    }

    // Record local pour matcher le body attendu par votre API Notification
    private record NotificationRequest(
            String notificationType,
            int templateId,
            List<String> to,
            Map<String, String> data
    ) {}
}