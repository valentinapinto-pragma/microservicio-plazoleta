package com.pragma.plazoleta.infrastructure.exceptionhandler;

import com.pragma.plazoleta.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ControllerAdvisorTest {

    private ControllerAdvisor controllerAdvisor;

    @BeforeEach
    void setUp() {
        controllerAdvisor = new ControllerAdvisor();
    }

    private MethodParameter getMethodParameter() {
        try {
            Method method = this.getClass().getDeclaredMethod("setUp");
            return new MethodParameter(method, -1);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    @DisplayName("Should handle business rule violations and return 400 Bad Request")
    void shouldHandleBusinessRuleViolation() {
        InvalidRestaurantDataException ex = new InvalidRestaurantDataException("Invalid restaurant data");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleBusinessRuleViolation(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid restaurant data", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle resource not found exceptions and return 404 Not Found")
    void shouldHandleResourceNotFound() {
        RestaurantNotFoundException ex = new RestaurantNotFoundException("Restaurant not found with id: 1");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleResourceNotFound(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Restaurant not found with id: 1", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle forbidden operations and return 403 Forbidden")
    void shouldHandleForbiddenOperation() {
        OrderDoesNotBelongToRestaurantException ex = new OrderDoesNotBelongToRestaurantException("The order does not belong to the restaurant");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleForbiddenOperation(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The order does not belong to the restaurant", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle UserNotAuthenticatedException and return 401 Unauthorized")
    void shouldHandleUserNotAuthenticated() {
        UserNotAuthenticatedException ex = new UserNotAuthenticatedException("The provided JWT token is invalid or has expired");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleUserNotAuthenticated(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The provided JWT token is invalid or has expired", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle uncategorized domain exceptions as 400 Bad Request instead of 500")
    void shouldHandleUncategorizedDomainException() {
        DomainException ex = new DomainException("Uncategorized business error") { };
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleUncategorizedDomainException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Uncategorized business error", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle Spring Security AccessDeniedException and return 403 Forbidden")
    void shouldHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Forbidden");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleAccessDeniedException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Access denied: You do not have permissions to perform this action", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors and return 400 Bad Request")
    void shouldHandleMethodArgumentNotValidExceptionWithFieldErrors() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "orderRequestDto");
        bindingResult.addError(new FieldError("orderRequestDto", "idRestaurant", "Restaurant id is required"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(getMethodParameter(), bindingResult);

        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleValidationExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("idRestaurant: Restaurant id is required", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with empty errors and return default message")
    void shouldHandleMethodArgumentNotValidExceptionEmpty() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "orderRequestDto");

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(getMethodParameter(), bindingResult);

        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleValidationExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid input data", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle unexpected exceptions and return 500 Internal Server Error")
    void shouldHandleGlobalException() {
        Exception ex = new RuntimeException("Unexpected database outage");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleGlobalException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected internal error occurred", response.getBody().getMessage());
    }
}
