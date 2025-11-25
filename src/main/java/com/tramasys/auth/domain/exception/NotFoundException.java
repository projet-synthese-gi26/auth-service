package com.tramasys.auth.domain.exception;

/**
 * Exception lancée lorsque une ressource du domaine n'est pas trouvée.
 */
public class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super(message);
    }
}
