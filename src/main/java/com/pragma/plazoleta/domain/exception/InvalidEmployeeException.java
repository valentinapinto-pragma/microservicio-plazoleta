package com.pragma.plazoleta.domain.exception;

public class InvalidEmployeeException extends BusinessRuleViolationException {
    public InvalidEmployeeException(String message) {
        super(message);
    }
}
