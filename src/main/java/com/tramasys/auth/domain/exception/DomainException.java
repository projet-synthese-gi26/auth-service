package com.tramasys.auth.domain.exception;

/**
 * Base pour les exceptions du domaine.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) { super(message); }
    public DomainException(String message, Throwable cause) { super(message, cause); }
}