package com.pragma.plazoleta.application.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestaurantResponseDtoTest {

    @Test
    @DisplayName("Should create RestaurantResponseDto and test getters and setters")
    void testGettersAndSetters() {
        RestaurantResponseDto dto = new RestaurantResponseDto("La Pampa", "https://logo.png");

        assertEquals("La Pampa", dto.getName());
        assertEquals("https://logo.png", dto.getUrlLogo());

        dto.setName("El Corral");
        dto.setUrlLogo("https://nuevo-logo.png");

        assertEquals("El Corral", dto.getName());
        assertEquals("https://nuevo-logo.png", dto.getUrlLogo());

        RestaurantResponseDto emptyDto = new RestaurantResponseDto();
        assertNotNull(emptyDto);
    }
}