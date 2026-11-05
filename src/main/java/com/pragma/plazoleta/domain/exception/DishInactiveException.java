package com.pragma.plazoleta.domain.exception;

public class DishInactiveException extends RuntimeException {
    public DishInactiveException(String message) {
        super(message);
    }
}
