package com.pragma.plazoleta.domain.exception;

public class InvalidRestaurantDataException extends BusinessRuleViolationException {
    public InvalidRestaurantDataException(String message) {
        super(message);
    }
}
