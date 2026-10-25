package com.pragma.plazoleta.application.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DishStatusRequestDtoTest {

    @Test
    @DisplayName("Should successfully instantiate and test getters and setters")
    void shouldTestGettersAndSetters() {
        DishStatusRequestDto dto = new DishStatusRequestDto();
        dto.setActive(true);

        assertTrue(dto.getActive());

        DishStatusRequestDto allArgsDto = new DishStatusRequestDto(false);
        assertFalse(allArgsDto.getActive());
    }
}
