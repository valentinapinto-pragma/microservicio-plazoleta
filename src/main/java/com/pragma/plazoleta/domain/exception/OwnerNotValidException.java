package com.pragma.plazoleta.domain.exception;

public class OwnerNotValidException extends BusinessRuleViolationException {
    public OwnerNotValidException(String message) {
        super(message);
    }
}
