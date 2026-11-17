package com.pragma.plazoleta.domain.exception;

public class InvalidOrderDataException extends BusinessRuleViolationException {
    public InvalidOrderDataException(String message) {
        super(message);
    }
}
