package com.pragma.plazoleta.domain.exception;

public class InvalidDishDataException extends BusinessRuleViolationException {
    public InvalidDishDataException(String message) {
        super(message);
    }
}
