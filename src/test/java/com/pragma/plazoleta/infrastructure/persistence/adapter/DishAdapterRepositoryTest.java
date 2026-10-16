package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.persistence.entity.CategoryEntity;
import com.pragma.plazoleta.infrastructure.persistence.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.DishEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.repository.DishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishAdapterRepositoryTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DishEntityMapper dishEntityMapper;

    @InjectMocks
    private DishAdapterRepository dishAdapterRepository;

    private Dish dish;
    private DishEntity entity;

    @BeforeEach
    void setUp() {
        Category category = new Category(1L, "Comida Rápida", "Hamburguesas y perros");
        CategoryEntity catEntity = new CategoryEntity(1L, "Comida Rápida", "Hamburguesas y perros");

        dish = new Dish(1L, "Perro Caliente", 12000, "Rico perro con tocineta", "https://img.com/perro.png", category, 2L, true);
        entity = new DishEntity(1L, "Perro Caliente", 12000, "Rico perro con tocineta", "https://img.com/perro.png", catEntity, 2L, true);
    }

    @Test
    @DisplayName("Should successfully save dish through repository and mapper")
    void shouldSaveDishSuccessfully() {
        when(dishEntityMapper.toEntity(dish)).thenReturn(entity);
        when(dishRepository.save(entity)).thenReturn(entity);
        when(dishEntityMapper.toDomain(entity)).thenReturn(dish);

        Dish result = dishAdapterRepository.saveDish(dish);

        assertNotNull(result);
        assertEquals(dish.getName(), result.getName());
        verify(dishEntityMapper).toEntity(dish);
        verify(dishRepository).save(entity);
        verify(dishEntityMapper).toDomain(entity);
    }
}
