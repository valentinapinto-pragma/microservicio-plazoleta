package com.pragma.plazoleta.domain.exception;

public class ClientHasActiveOrderException extends BusinessRuleViolationException {
    public ClientHasActiveOrderException(String message) {
        super(message);
    }
}
