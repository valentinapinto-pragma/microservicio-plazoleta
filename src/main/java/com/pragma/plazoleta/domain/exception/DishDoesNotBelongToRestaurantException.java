package com.pragma.plazoleta.domain.exception;

public class DishDoesNotBelongToRestaurantException extends BusinessRuleViolationException {
    public DishDoesNotBelongToRestaurantException(String message) {
        super(message);
    }
}
