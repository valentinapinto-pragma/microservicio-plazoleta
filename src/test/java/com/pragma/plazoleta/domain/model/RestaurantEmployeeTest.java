package com.pragma.plazoleta.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantEmployeeTest {

    @Test
    @DisplayName("Should create RestaurantEmployee with all args constructor and verify getters and setters")
    void shouldCreateRestaurantEmployeeAndVerifyGettersSetters() {
        // Arrange & Act
        RestaurantEmployee employee = new RestaurantEmployee(1L, 10L, 20L);

        // Assert
        assertEquals(1L, employee.getId());
        assertEquals(10L, employee.getRestaurantId());
        assertEquals(20L, employee.getEmployeeId());

        // Act (Setters)
        employee.setId(2L);
        employee.setRestaurantId(15L);
        employee.setEmployeeId(25L);

        // Assert
        assertEquals(2L, employee.getId());
        assertEquals(15L, employee.getRestaurantId());
        assertEquals(25L, employee.getEmployeeId());
    }

    @Test
    @DisplayName("Should create RestaurantEmployee with no args constructor")
    void shouldCreateRestaurantEmployeeWithNoArgsConstructor() {
        // Arrange & Act
        RestaurantEmployee employee = new RestaurantEmployee();

        // Assert
        assertNotNull(employee);
        assertNull(employee.getId());
        assertNull(employee.getRestaurantId());
        assertNull(employee.getEmployeeId());
    }
}