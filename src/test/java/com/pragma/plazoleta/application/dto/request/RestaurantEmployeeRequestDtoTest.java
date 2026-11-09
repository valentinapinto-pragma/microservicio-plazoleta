package com.pragma.plazoleta.application.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RestaurantEmployeeRequestDtoTest {

    @Test
    @DisplayName("Should test getters, setters and constructors of RestaurantEmployeeRequestDto")
    void shouldTestGettersSettersAndConstructors() {
        RestaurantEmployeeRequestDto dto = new RestaurantEmployeeRequestDto();
        assertNull(dto.getEmployeeId());

        dto.setEmployeeId(10L);
        assertEquals(10L, dto.getEmployeeId());

        RestaurantEmployeeRequestDto dtoWithArgs = new RestaurantEmployeeRequestDto(20L);
        assertEquals(20L, dtoWithArgs.getEmployeeId());
        assertNotNull(dtoWithArgs);
    }
}