package com.pragma.plazoleta.domain.exception;

public class OrderDoesNotBelongToRestaurantException extends ForbiddenOperationException {
    public OrderDoesNotBelongToRestaurantException(String message) {
        super(message);
    }
}
