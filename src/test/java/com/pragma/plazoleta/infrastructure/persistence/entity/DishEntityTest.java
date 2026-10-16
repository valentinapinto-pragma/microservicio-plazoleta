package com.pragma.plazoleta.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DishEntityTest {

    @Test
    @DisplayName("Should test DishEntity all args constructor and getters")
    void testDishEntity() {
        CategoryEntity cat = new CategoryEntity(1L, "Carnes", "Cortes selectos");
        DishEntity entity = new DishEntity(1L, "Churrasco", 35000, "300g de carne", "https://img.com/carne.png", cat, 5L, true);

        assertEquals(1L, entity.getId());
        assertEquals("Churrasco", entity.getName());
        assertEquals(35000, entity.getPrice());
        assertEquals("300g de carne", entity.getDescription());
        assertEquals("https://img.com/carne.png", entity.getUrlImage());
        assertEquals(cat, entity.getCategory());
        assertEquals(5L, entity.getIdRestaurant());
        assertTrue(entity.getActive());

        DishEntity empty = new DishEntity();
        empty.setActive(false);
        assertFalse(empty.getActive());
    }
}
