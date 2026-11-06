package com.pragma.plazoleta.domain.exception;

public class EmployeeNotAssignedToRestaurantException extends RuntimeException {
    public EmployeeNotAssignedToRestaurantException(String message) {
        super(message);
    }
}
