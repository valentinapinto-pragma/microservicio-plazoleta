package com.pragma.plazoleta.domain.exception;

public class DishInactiveException extends BusinessRuleViolationException {
    public DishInactiveException(String message) {
        super(message);
    }
}
