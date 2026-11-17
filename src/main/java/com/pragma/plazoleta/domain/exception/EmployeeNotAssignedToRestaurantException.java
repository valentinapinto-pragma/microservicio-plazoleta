package com.pragma.plazoleta.domain.exception;

public class EmployeeNotAssignedToRestaurantException extends ForbiddenOperationException {
    public EmployeeNotAssignedToRestaurantException(String message) {
        super(message);
    }
}
