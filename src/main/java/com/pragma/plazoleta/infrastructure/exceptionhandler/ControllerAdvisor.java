package com.pragma.plazoleta.infrastructure.exceptionhandler;

import com.pragma.plazoleta.domain.exception.BusinessRuleViolationException;
import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.exception.ForbiddenOperationException;
import com.pragma.plazoleta.domain.exception.ResourceNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotAuthenticatedException;
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

    private static final String ACCESS_DENIED_MESSAGE = "Access denied: You do not have permissions to perform this action";
    private static final String INVALID_INPUT_MESSAGE = "Invalid input data";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected internal error occurred";

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessRuleViolation(BusinessRuleViolationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponseDto> handleForbiddenOperation(ForbiddenOperationException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotAuthenticated(UserNotAuthenticatedException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponseDto> handleUncategorizedDomainException(DomainException ex) {
        log.warn("Domain exception without a specific category: {}", ex.getClass().getSimpleName());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED_MESSAGE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String combinedErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, combinedErrors.isEmpty() ? INVALID_INPUT_MESSAGE : combinedErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {
        log.error("Unhandled internal exception: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR_MESSAGE);
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponseDto(message));
    }
}
