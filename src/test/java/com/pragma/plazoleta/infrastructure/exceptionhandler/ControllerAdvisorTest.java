package com.pragma.plazoleta.infrastructure.exceptionhandler;

import com.pragma.plazoleta.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ControllerAdvisorTest {

    private ControllerAdvisor controllerAdvisor;

    @BeforeEach
    void setUp() {
        controllerAdvisor = new ControllerAdvisor();
    }

    @Test
    @DisplayName("Should handle InvalidRestaurantDataException")
    void shouldHandleInvalidRestaurantDataException() {
        InvalidRestaurantDataException ex = new InvalidRestaurantDataException("Invalid restaurant data");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleInvalidRestaurantDataException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid restaurant data", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle OwnerNotValidException")
    void shouldHandleOwnerNotValidException() {
        OwnerNotValidException ex = new OwnerNotValidException("Owner is not valid");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleOwnerNotValidException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Owner is not valid", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors")
    void shouldHandleMethodArgumentNotValidExceptionWithFieldErrors() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "orderRequestDto");
        bindingResult.addError(new FieldError("orderRequestDto", "idRestaurant", "Restaurant id is required"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("idRestaurant: Restaurant id is required", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with empty field errors")
    void shouldHandleMethodArgumentNotValidExceptionEmpty() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "orderRequestDto");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid input data", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle InvalidDishDataException")
    void shouldHandleInvalidDishDataException() {
        InvalidDishDataException ex = new InvalidDishDataException("Invalid dish data");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleInvalidDishDataException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid dish data", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle RestaurantNotFoundException")
    void shouldHandleRestaurantNotFoundException() {
        RestaurantNotFoundException ex = new RestaurantNotFoundException("Restaurant not found");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleRestaurantNotFoundException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Restaurant not found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle DishNotFoundException")
    void shouldHandleDishNotFoundException() {
        DishNotFoundException ex = new DishNotFoundException("Dish not found");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleDishNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dish not found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle UserNotOwnerOfRestaurantException")
    void shouldHandleUserNotOwnerOfRestaurantException() {
        UserNotOwnerOfRestaurantException ex = new UserNotOwnerOfRestaurantException("Not the owner");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleUserNotOwnerOfRestaurantException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not the owner", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle AccessDeniedException")
    void shouldHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Forbidden");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleAccessDeniedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle InvalidOrderDataException")
    void shouldHandleInvalidOrderDataException() {
        InvalidOrderDataException ex = new InvalidOrderDataException("Invalid order data");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleInvalidOrderDataException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid order data", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle EmployeeNotAssignedToRestaurantException")
    void shouldHandleEmployeeNotAssignedToRestaurantException() {
        EmployeeNotAssignedToRestaurantException ex = new EmployeeNotAssignedToRestaurantException("The employee is not assigned to any restaurant");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleEmployeeNotAssignedToRestaurantException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The employee is not assigned to any restaurant", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle ClientHasActiveOrderException")
    void shouldHandleClientHasActiveOrderException() {
        ClientHasActiveOrderException ex = new ClientHasActiveOrderException("Active order in progress");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleClientHasActiveOrderException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Active order in progress", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle DishDoesNotBelongToRestaurantException")
    void shouldHandleDishDoesNotBelongToRestaurantException() {
        DishDoesNotBelongToRestaurantException ex = new DishDoesNotBelongToRestaurantException("Dish does not belong");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleDishDoesNotBelongToRestaurantException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dish does not belong", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle DishInactiveException")
    void shouldHandleDishInactiveException() {
        DishInactiveException ex = new DishInactiveException("Dish is inactive");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleDishInactiveException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dish is inactive", response.getBody().getMessage());
    }
    @Test
    @DisplayName("Should handle OrderNotFoundException")
    void shouldHandleOrderNotFoundException() {
        OrderNotFoundException ex = new OrderNotFoundException("Order not found with id: 100");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleOrderNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Order not found with id: 100", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle OrderDoesNotBelongToRestaurantException")
    void shouldHandleOrderDoesNotBelongToRestaurantException() {
        OrderDoesNotBelongToRestaurantException ex = new OrderDoesNotBelongToRestaurantException("The order does not belong to the restaurant where the employee works");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleOrderDoesNotBelongToRestaurantException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The order does not belong to the restaurant where the employee works", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle InvalidOrderStatusException")
    void shouldHandleInvalidOrderStatusException() {
        InvalidOrderStatusException ex = new InvalidOrderStatusException("Only orders in PENDIENTE status can be assigned to a chef");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleInvalidOrderStatusException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Only orders in PENDIENTE status can be assigned to a chef", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle EmployeeAlreadyAssignedException")
    void shouldHandleEmployeeAlreadyAssignedException() {
        EmployeeAlreadyAssignedException ex = new EmployeeAlreadyAssignedException("Employee already assigned");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleEmployeeAlreadyAssignedException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Employee already assigned", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle InvalidEmployeeException")
    void shouldHandleInvalidEmployeeException() {
        InvalidEmployeeException ex = new InvalidEmployeeException("Invalid employee");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleInvalidEmployeeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid employee", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle InvalidSecurityPinException and return 400 Bad Request")
    void shouldHandleInvalidSecurityPinException() {
        InvalidSecurityPinException ex = new InvalidSecurityPinException("Invalid security PIN");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleInvalidSecurityPinException(ex);

        assertNotNull(response);
        ErrorResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid security PIN", body.getMessage());
    }

    @Test
    @DisplayName("Should handle OrderCannotBeCancelledException and return 400 Bad Request")
    void shouldHandleOrderCannotBeCancelledException() {
        OrderCannotBeCancelledException ex = new OrderCannotBeCancelledException("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleOrderCannotBeCancelledException(ex);

        assertNotNull(response);
        ErrorResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse", body.getMessage());
    }

    @Test
    @DisplayName("Should handle OrderDoesNotBelongToClientException and return 403 Forbidden")
    void shouldHandleOrderDoesNotBelongToClientException() {
        OrderDoesNotBelongToClientException ex = new OrderDoesNotBelongToClientException("The order does not belong to the authenticated client");
        ResponseEntity<ErrorResponseDto> response = controllerAdvisor.handleOrderDoesNotBelongToClientException(ex);

        assertNotNull(response);
        ErrorResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("The order does not belong to the authenticated client", body.getMessage());
    }
}