package com.pragma.plazoleta.domain.exception;

public class OrderDoesNotBelongToClientException extends RuntimeException {
    public OrderDoesNotBelongToClientException(String message) {
        super(message);
    }
}
