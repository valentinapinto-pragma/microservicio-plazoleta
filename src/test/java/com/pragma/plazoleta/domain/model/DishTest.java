package com.pragma.plazoleta.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DishTest {

    @Test
    @DisplayName("Should create dish using all args constructor and getters")
    void dish_AllArgsConstructorAndGetters() {
        Category category = new Category(1L, "Plato Fuerte", "Platos principales");
        Dish dish = new Dish(
                1L,
                "Bandeja Paisa",
                25000,
                "Frijoles, arroz, carne molida",
                "https://img.com/bandeja.png",
                category,
                10L,
                true
        );

        assertEquals(1L, dish.getId());
        assertEquals("Bandeja Paisa", dish.getName());
        assertEquals(25000, dish.getPrice());
        assertEquals("Frijoles, arroz, carne molida", dish.getDescription());
        assertEquals("https://img.com/bandeja.png", dish.getUrlImage());
        assertEquals(category, dish.getCategory());
        assertEquals(10L, dish.getIdRestaurant());
        assertTrue(dish.getActive());
    }

    @Test
    @DisplayName("Should set and get properties using no args constructor and setters")
    void dish_NoArgsConstructorAndSetters() {
        Category category = new Category(2L, "Bebidas", "Jugos naturales");
        Dish dish = new Dish();
        dish.setId(2L);
        dish.setName("Limonada de Coco");
        dish.setPrice(8000);
        dish.setDescription("Refrescante bebida");
        dish.setUrlImage("https://img.com/limonada.png");
        dish.setCategory(category);
        dish.setIdRestaurant(15L);
        dish.setActive(false);

        assertEquals(2L, dish.getId());
        assertEquals("Limonada de Coco", dish.getName());
        assertEquals(8000, dish.getPrice());
        assertEquals("Refrescante bebida", dish.getDescription());
        assertEquals("https://img.com/limonada.png", dish.getUrlImage());
        assertEquals(category, dish.getCategory());
        assertEquals(15L, dish.getIdRestaurant());
        assertFalse(dish.getActive());
    }
}
