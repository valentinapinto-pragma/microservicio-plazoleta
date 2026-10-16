package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.domain.model.Dish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class DishDtoMapperTest {

    private final DishDtoMapper mapper = Mappers.getMapper(DishDtoMapper.class);

    @Test
    @DisplayName("Should map DishRequestDto to Dish correctly")
    void shouldMapDtoToDomain() {
        DishRequestDto dto = new DishRequestDto("Pizza", 20000, "Queso y jamón", "https://img.com/pizza.png", 2L, 5L);

        Dish domain = mapper.toDomain(dto);

        assertNotNull(domain);
        assertEquals("Pizza", domain.getName());
        assertEquals(20000, domain.getPrice());
        assertEquals("Queso y jamón", domain.getDescription());
        assertEquals("https://img.com/pizza.png", domain.getUrlImage());
        assertNotNull(domain.getCategory());
        assertEquals(2L, domain.getCategory().getId());
        assertEquals(5L, domain.getIdRestaurant());
    }

    @Test
    @DisplayName("Should return null when dto is null")
    void shouldReturnNullWhenDtoIsNull() {
        assertNull(mapper.toDomain(null));
    }
}
