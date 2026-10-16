package com.pragma.plazoleta.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantEntityTest {

    @Test
    @DisplayName("Should test all args constructor and getters of RestaurantEntity")
    void testAllArgsConstructorAndGetters() {
        RestaurantEntity entity = new RestaurantEntity(
                1L,
                "Restaurante Norte",
                "900555666",
                "Carrera 7 # 100",
                "+573009998877",
                "https://logo.com/foto.png",
                3L
        );

        assertEquals(1L, entity.getId());
        assertEquals("Restaurante Norte", entity.getName());
        assertEquals("900555666", entity.getNit());
        assertEquals("Carrera 7 # 100", entity.getAddress());
        assertEquals("+573009998877", entity.getPhone());
        assertEquals("https://logo.com/foto.png", entity.getUrlLogo());
        assertEquals(3L, entity.getIdOwner());
    }

    @Test
    @DisplayName("Should test no args constructor and setters of RestaurantEntity")
    void testNoArgsConstructorAndSetters() {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setId(2L);
        entity.setName("Restaurante Sur");
        entity.setNit("800333222");
        entity.setAddress("Calle 30 Sur");
        entity.setPhone("3101112233");
        entity.setUrlLogo("https://logo.com/sur.png");
        entity.setIdOwner(4L);

        assertEquals(2L, entity.getId());
        assertEquals("Restaurante Sur", entity.getName());
        assertEquals("800333222", entity.getNit());
        assertEquals("Calle 30 Sur", entity.getAddress());
        assertEquals("3101112233", entity.getPhone());
        assertEquals("https://logo.com/sur.png", entity.getUrlLogo());
        assertEquals(4L, entity.getIdOwner());
    }
}
