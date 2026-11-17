package com.pragma.plazoleta.domain.exception;

public class UserNotAuthenticatedException extends DomainException {

    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
