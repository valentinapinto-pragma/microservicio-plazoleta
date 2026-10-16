package com.pragma.plazoleta.infrastructure.persistence.mapper;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.persistence.entity.CategoryEntity;
import com.pragma.plazoleta.infrastructure.persistence.entity.DishEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class DishEntityMapperTest {

    private final DishEntityMapper mapper = Mappers.getMapper(DishEntityMapper.class);

    @Test
    @DisplayName("Should map Dish domain model to DishEntity correctly")
    void shouldMapDomainToEntity() {
        Category category = new Category(1L, "Bebidas", "Jugos");
        Dish dish = new Dish(1L, "Limonada", 5000, "Refrescante", "https://img.com/limo.png", category, 2L, true);

        DishEntity entity = mapper.toEntity(dish);

        assertNotNull(entity);
        assertEquals(dish.getId(), entity.getId());
        assertEquals(dish.getName(), entity.getName());
        assertEquals(dish.getPrice(), entity.getPrice());
        assertEquals(dish.getDescription(), entity.getDescription());
        assertEquals(dish.getUrlImage(), entity.getUrlImage());
        assertNotNull(entity.getCategory());
        assertEquals(dish.getCategory().getId(), entity.getCategory().getId());
        assertEquals(dish.getIdRestaurant(), entity.getIdRestaurant());
        assertEquals(dish.getActive(), entity.getActive());
    }

    @Test
    @DisplayName("Should map DishEntity to Dish domain model correctly")
    void shouldMapEntityToDomain() {
        CategoryEntity catEntity = new CategoryEntity(1L, "Bebidas", "Jugos");
        DishEntity entity = new DishEntity(1L, "Limonada", 5000, "Refrescante", "https://img.com/limo.png", catEntity, 2L, true);

        Dish domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getName(), domain.getName());
        assertEquals(entity.getPrice(), domain.getPrice());
        assertEquals(entity.getDescription(), domain.getDescription());
        assertEquals(entity.getUrlImage(), domain.getUrlImage());
        assertNotNull(domain.getCategory());
        assertEquals(entity.getCategory().getId(), domain.getCategory().getId());
        assertEquals(entity.getIdRestaurant(), domain.getIdRestaurant());
        assertEquals(entity.getActive(), domain.getActive());
    }

    @Test
    @DisplayName("Should return null when input is null")
    void shouldReturnNullWhenInputsAreNull() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDomain(null));
    }
}
