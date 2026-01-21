package com.tramasys.auth.domain.port.out;

import java.util.Map;

public interface NotificationServicePort {
    /**
     * Envoie l'email de réinitialisation contenant le code.
     * @param email L'email du destinataire
     * @param variables Les variables à injecter dans le template (ex: "code": "123456", "username": "Brayan")
     */
    void sendPasswordResetEmail(String email, Map<String, String> variables);
}