package com.pragma.plazoleta.domain.exception;

public class InvalidDishDataException extends RuntimeException {
    public InvalidDishDataException(String message) {
        super(message);
    }
}
