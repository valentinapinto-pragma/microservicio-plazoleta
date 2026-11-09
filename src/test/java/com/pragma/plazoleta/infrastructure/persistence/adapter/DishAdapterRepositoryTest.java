package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.CustomPage;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    @DisplayName("Should delete dish by id through repository")
    void shouldDeleteDishById() {
        doNothing().when(dishRepository).deleteById(1L);

        dishAdapterRepository.delete(1L);

        verify(dishRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should return paged dishes filtered by category when categoryId is provided")
    void shouldReturnDishesFilteredByCategory() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<DishEntity> page = new PageImpl<>(List.of(entity));

        when(dishRepository.findByIdRestaurantAndCategoryIdAndActiveTrue(2L, 1L, pageable)).thenReturn(page);
        when(dishEntityMapper.toDomain(entity)).thenReturn(dish);

        CustomPage<Dish> result = dishAdapterRepository.getDishesByRestaurant(2L, 1L, 0, 5);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(dish.getName(), result.getContent().get(0).getName());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(dishRepository).findByIdRestaurantAndCategoryIdAndActiveTrue(2L, 1L, pageable);
    }

    @Test
    @DisplayName("Should return paged dishes without category filter when categoryId is null")
    void shouldReturnDishesWithoutCategoryFilterWhenCategoryIdIsNull() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<DishEntity> page = new PageImpl<>(List.of(entity));

        when(dishRepository.findByIdRestaurantAndActiveTrue(2L, pageable)).thenReturn(page);
        when(dishEntityMapper.toDomain(entity)).thenReturn(dish);

        CustomPage<Dish> result = dishAdapterRepository.getDishesByRestaurant(2L, null, 0, 5);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(dish.getName(), result.getContent().get(0).getName());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(dishRepository).findByIdRestaurantAndActiveTrue(2L, pageable);
    }
}