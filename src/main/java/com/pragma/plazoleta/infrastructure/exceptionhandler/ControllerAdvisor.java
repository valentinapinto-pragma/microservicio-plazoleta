package com.pragma.plazoleta.infrastructure.exceptionhandler;

import com.pragma.plazoleta.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler({
            InvalidRestaurantDataException.class,
            OwnerNotValidException.class,
            InvalidDishDataException.class,
            InvalidOrderDataException.class,
            EmployeeNotAssignedToRestaurantException.class,
            ClientHasActiveOrderException.class,
            DishDoesNotBelongToRestaurantException.class,
            DishInactiveException.class,
            RestaurantNotFoundException.class,
            OrderDoesNotBelongToRestaurantException.class,
            InvalidOrderStatusException.class,
            EmployeeAlreadyAssignedException.class,
            InvalidEmployeeException.class,
            InvalidSecurityPinException.class,
            OrderCannotBeCancelledException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequestExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler({
            DishNotFoundException.class,
            OrderNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDto> handleNotFoundExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler({
            UserNotOwnerOfRestaurantException.class,
            OrderDoesNotBelongToClientException.class
    })
    public ResponseEntity<ErrorResponseDto> handleForbiddenExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto("Access denied: You do not have permissions to perform this action"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String combinedErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(combinedErrors.isEmpty() ? "Invalid input data" : combinedErrors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {
        log.error("Unhandled internal exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("An unexpected internal error occurred"));
    }
}