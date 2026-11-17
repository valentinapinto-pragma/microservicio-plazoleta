package com.pragma.plazoleta.domain.exception;

public class RestaurantNotFoundException extends ResourceNotFoundException {
    public RestaurantNotFoundException(String message) {
        super(message);
    }
}
