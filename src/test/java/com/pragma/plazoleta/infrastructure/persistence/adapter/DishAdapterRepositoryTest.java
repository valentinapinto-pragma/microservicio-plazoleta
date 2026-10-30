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

import java.util.List;
import java.util.Optional;

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

        Dish result = dishAdapterRepository.save(dish);

        assertNotNull(result);
        assertEquals(dish.getName(), result.getName());
        verify(dishEntityMapper).toEntity(dish);
        verify(dishRepository).save(entity);
        verify(dishEntityMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should find dish by id through repository and mapper")
    void shouldFindDishById() {
        when(dishRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(dishEntityMapper.toDomain(entity)).thenReturn(dish);

        Optional<Dish> result = dishAdapterRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(dish.getName(), result.get().getName());
        verify(dishRepository).findById(1L);
        verify(dishEntityMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should find all dishes through repository and mapper")
    void shouldFindAllDishes() {
        when(dishRepository.findAll()).thenReturn(List.of(entity));
        when(dishEntityMapper.toDomain(entity)).thenReturn(dish);

        List<Dish> result = dishAdapterRepository.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dish.getName(), result.get(0).getName());
        verify(dishRepository).findAll();
        verify(dishEntityMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should update dish successfully through repository and mapper")
    void shouldUpdateDishSuccessfully() {
        when(dishEntityMapper.toEntity(dish)).thenReturn(entity);
        when(dishRepository.save(entity)).thenReturn(entity);
        when(dishEntityMapper.toDomain(entity)).thenReturn(dish);

        Dish result = dishAdapterRepository.update(dish);

        assertNotNull(result);
        assertEquals(dish.getName(), result.getName());
        verify(dishEntityMapper).toEntity(dish);
        verify(dishRepository).save(entity);
        verify(dishEntityMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should delete dish by id through repository")
    void shouldDeleteDishById() {
        doNothing().when(dishRepository).deleteById(1L);

        dishAdapterRepository.delete(1L);

        verify(dishRepository).deleteById(1L);
    }
}