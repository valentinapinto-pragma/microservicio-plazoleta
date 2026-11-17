package com.pragma.plazoleta.domain.exception;

public class InvalidOrderStatusException extends BusinessRuleViolationException {
    public InvalidOrderStatusException(String message) {
        super(message);
    }
}
