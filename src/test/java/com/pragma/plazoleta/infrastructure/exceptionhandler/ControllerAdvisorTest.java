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
    @DisplayName("Should handle bad request exceptions and return 400 Bad Request")
    void shouldHandleBadRequestExceptions() {
        InvalidRestaurantDataException ex = new InvalidRestaurantDataException("Invalid restaurant data");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleBadRequestExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid restaurant data", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle not found exceptions and return 404 Not Found")
    void shouldHandleNotFoundExceptions() {
        DishNotFoundException dishEx = new DishNotFoundException("Dish not found with id: 1");
        ResponseEntity<ErrorResponseDto> dishResponse = controllerAdvisor.handleNotFoundExceptions(dishEx);

        assertNotNull(dishResponse);
        assertEquals(HttpStatus.NOT_FOUND, dishResponse.getStatusCode());
        assertNotNull(dishResponse.getBody());
        assertEquals("Dish not found with id: 1", dishResponse.getBody().getMessage());

        OrderNotFoundException orderEx = new OrderNotFoundException("Order not found with id: 10");
        ResponseEntity<ErrorResponseDto> orderResponse = controllerAdvisor.handleNotFoundExceptions(orderEx);

        assertNotNull(orderResponse);
        assertEquals(HttpStatus.NOT_FOUND, orderResponse.getStatusCode());
        assertNotNull(orderResponse.getBody());
        assertEquals("Order not found with id: 10", orderResponse.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle forbidden business exceptions and return 403 Forbidden")
    void shouldHandleForbiddenExceptions() {
        UserNotOwnerOfRestaurantException ex = new UserNotOwnerOfRestaurantException("The user is not the owner");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleForbiddenExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The user is not the owner", response.getBody().getMessage());
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
