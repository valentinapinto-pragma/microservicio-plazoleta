package com.pragma.plazoleta.domain.exception;

public class OrderDoesNotBelongToClientException extends ForbiddenOperationException {
    public OrderDoesNotBelongToClientException(String message) {
        super(message);
    }
}
