package com.tramasys.auth.domain.exception;

/**
 * Exception pour identifiants invalides.
 */
public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException(String message) { super(message); }
}
