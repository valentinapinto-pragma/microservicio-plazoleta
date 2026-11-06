package com.pragma.plazoleta.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantEmployeeEntityTest {

    @Test
    @DisplayName("Should verify RestaurantEmployeeEntity constructor, getters and setters")
    void shouldVerifyGettersAndSetters() {
        RestaurantEmployeeEntity entity = new RestaurantEmployeeEntity(1L, 10L, 20L);

        assertEquals(1L, entity.getId());
        assertEquals(10L, entity.getRestaurantId());
        assertEquals(20L, entity.getEmployeeId());

        entity.setId(2L);
        entity.setRestaurantId(15L);
        entity.setEmployeeId(25L);

        assertEquals(2L, entity.getId());
        assertEquals(15L, entity.getRestaurantId());
        assertEquals(25L, entity.getEmployeeId());
    }

    @Test
    @DisplayName("Should verify no args constructor")
    void shouldVerifyNoArgsConstructor() {
        RestaurantEmployeeEntity entity = new RestaurantEmployeeEntity();
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getRestaurantId());
        assertNull(entity.getEmployeeId());
    }
}