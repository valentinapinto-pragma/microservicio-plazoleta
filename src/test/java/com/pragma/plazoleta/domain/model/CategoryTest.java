package com.pragma.plazoleta.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    @DisplayName("Should create category using all args constructor and getters")
    void category_AllArgsConstructorAndGetters() {
        Category category = new Category(1L, "Postres", "Postres y dulces tradicionales");

        assertEquals(1L, category.getId());
        assertEquals("Postres", category.getName());
        assertEquals("Postres y dulces tradicionales", category.getDescription());
    }

    @Test
    @DisplayName("Should set and get properties using no args constructor and setters")
    void category_NoArgsConstructorAndSetters() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Entradas");
        category.setDescription("Aperitivos para compartir");

        assertEquals(2L, category.getId());
        assertEquals("Entradas", category.getName());
        assertEquals("Aperitivos para compartir", category.getDescription());
    }
}
