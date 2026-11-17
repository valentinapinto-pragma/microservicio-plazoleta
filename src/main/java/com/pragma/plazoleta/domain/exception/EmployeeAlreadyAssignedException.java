package com.pragma.plazoleta.domain.exception;

public class EmployeeAlreadyAssignedException extends BusinessRuleViolationException {
    public EmployeeAlreadyAssignedException(String message) {
        super(message);
    }
}
