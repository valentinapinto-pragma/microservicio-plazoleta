package com.pragma.plazoleta.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomPageTest {

    @Test
    @DisplayName("Should correctly instantiate and return values from CustomPage")
    void shouldCreateCustomPageSuccessfully() {
        List<String> items = List.of("Item 1", "Item 2");
        CustomPage<String> page = new CustomPage<>(items, 0, 10, 2L, 1);

        assertNotNull(page);
        assertEquals(items, page.getContent());
        assertEquals(0, page.getCurrentPage());
        assertEquals(10, page.getPageSize());
        assertEquals(2L, page.getTotalElements());
        assertEquals(1, page.getTotalPages());
    }
}