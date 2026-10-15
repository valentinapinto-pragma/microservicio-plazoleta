package com.pragma.plazoleta.domain.exception;

public class InvalidRestaurantDataException extends RuntimeException {
    public InvalidRestaurantDataException(String message) {
        super(message);
    }
}
