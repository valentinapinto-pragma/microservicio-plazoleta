package com.pragma.plazoleta.domain.exception;

public class InvalidSecurityPinException extends RuntimeException {
    public InvalidSecurityPinException(String message) {
        super(message);
    }
}
