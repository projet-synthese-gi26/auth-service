package com.tramasys.auth.domain.exception;

/**
 * Exception lancée lorsqu'une ressource existe déjà (ex: username/email duplicate).
 */
public class DuplicateResourceException extends DomainException {
    public DuplicateResourceException(String message) { super(message); }
}
