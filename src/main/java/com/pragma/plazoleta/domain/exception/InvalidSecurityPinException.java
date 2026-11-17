package com.pragma.plazoleta.domain.exception;

public class InvalidSecurityPinException extends BusinessRuleViolationException {
    public InvalidSecurityPinException(String message) {
        super(message);
    }
}
