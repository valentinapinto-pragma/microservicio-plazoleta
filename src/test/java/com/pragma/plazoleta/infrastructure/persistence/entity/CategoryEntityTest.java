package com.pragma.plazoleta.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryEntityTest {

    @Test
    @DisplayName("Should test CategoryEntity constructors and getters")
    void testCategoryEntity() {
        CategoryEntity entity = new CategoryEntity(1L, "Sopas", "Sopas caseras");

        assertEquals(1L, entity.getId());
        assertEquals("Sopas", entity.getName());
        assertEquals("Sopas caseras", entity.getDescription());

        CategoryEntity empty = new CategoryEntity();
        empty.setId(2L);
        empty.setName("Ensaladas");
        empty.setDescription("Frescas y saludables");

        assertEquals(2L, empty.getId());
        assertEquals("Ensaladas", empty.getName());
        assertEquals("Frescas y saludables", empty.getDescription());
    }
}
