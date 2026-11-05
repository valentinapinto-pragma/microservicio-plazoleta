package com.pragma.plazoleta.domain.exception;

public class DishDoesNotBelongToRestaurantException extends RuntimeException {
    public DishDoesNotBelongToRestaurantException(String message) {
        super(message);
    }
}
