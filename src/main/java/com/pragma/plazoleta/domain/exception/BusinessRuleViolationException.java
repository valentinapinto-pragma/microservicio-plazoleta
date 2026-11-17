package com.pragma.plazoleta.domain.exception;

public abstract class BusinessRuleViolationException extends DomainException {

    protected BusinessRuleViolationException(String message) {
        super(message);
    }
}
