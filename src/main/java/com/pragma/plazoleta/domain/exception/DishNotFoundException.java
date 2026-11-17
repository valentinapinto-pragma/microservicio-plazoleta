package com.pragma.plazoleta.domain.exception;

public class DishNotFoundException extends ResourceNotFoundException {
    public DishNotFoundException(String message) {
        super(message);
    }
}
