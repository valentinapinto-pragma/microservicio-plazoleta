package com.pragma.plazoleta.domain.exception;

public class OrderCannotBeCancelledException extends BusinessRuleViolationException {
    public OrderCannotBeCancelledException(String message) {
        super(message);
    }
}
