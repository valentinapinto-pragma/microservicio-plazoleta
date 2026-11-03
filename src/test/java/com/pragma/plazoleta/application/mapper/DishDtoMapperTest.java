package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

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

    @Test
    @DisplayName("Should map Dish to DishResponseDto correctly")
    void shouldMapDomainToResponseDto() {
        Category category = new Category(2L, "Comida Rápida", "Pizzas y hamburguesas");
        Dish dish = new Dish(1L, "Pizza", 20000, "Queso y jamón", "https://img.com/pizza.png", category, 5L, true);

        DishResponseDto responseDto = mapper.toResponseDto(dish);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getId());
        assertEquals("Pizza", responseDto.getName());
        assertEquals(20000, responseDto.getPrice());
        assertEquals("Queso y jamón", responseDto.getDescription());
        assertEquals("https://img.com/pizza.png", responseDto.getUrlImage());
        assertEquals("Comida Rápida", responseDto.getCategory());
    }

    @Test
    @DisplayName("Should return null when dish is null")
    void shouldReturnNullWhenDishIsNull() {
        assertNull(mapper.toResponseDto(null));
    }

    @Test
    @DisplayName("Should map to null category name when category is null")
    void shouldMapNullCategoryWhenCategoryIsNull() {
        Dish dish = new Dish(1L, "Pizza", 20000, "Queso", "https://img.com/pizza.png", null, 5L, true);

        DishResponseDto responseDto = mapper.toResponseDto(dish);

        assertNotNull(responseDto);
        assertNull(responseDto.getCategory());
    }

    @Test
    @DisplayName("Should map to null category name when category name is null")
    void shouldMapNullCategoryWhenCategoryNameIsNull() {
        Category category = new Category(2L, null, null);
        Dish dish = new Dish(1L, "Pizza", 20000, "Queso", "https://img.com/pizza.png", category, 5L, true);

        DishResponseDto responseDto = mapper.toResponseDto(dish);

        assertNotNull(responseDto);
        assertNull(responseDto.getCategory());
    }

    @Test
    @DisplayName("Should map list of Dish to list of DishResponseDto")
    void shouldMapDomainListToResponseDtoList() {
        Category category = new Category(2L, "Bebidas", "Jugos naturales");
        Dish dish = new Dish(1L, "Limonada", 5000, "Natural", "https://img.com/jugo.png", category, 5L, true);

        List<DishResponseDto> responseList = mapper.toResponseDtoList(List.of(dish));

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals("Limonada", responseList.get(0).getName());
    }

    @Test
    @DisplayName("Should return null when domain list is null")
    void shouldReturnNullWhenDomainListIsNull() {
        assertNull(mapper.toResponseDtoList(null));
    }
}