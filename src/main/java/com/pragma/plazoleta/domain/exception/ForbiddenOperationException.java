package com.pragma.plazoleta.domain.exception;

public abstract class ForbiddenOperationException extends DomainException {

    protected ForbiddenOperationException(String message) {
        super(message);
    }
}
