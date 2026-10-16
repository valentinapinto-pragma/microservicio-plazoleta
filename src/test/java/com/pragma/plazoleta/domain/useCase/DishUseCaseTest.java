package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.InvalidDishDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
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
class DishUseCaseTest {

    @Mock
    private DishPersistentPort dishPersistentPort;

    @Mock
    private RestaurantPersistentPort restaurantPersistentPort;

    @InjectMocks
    private DishUseCase dishUseCase;

    private Dish validDish;

    @BeforeEach
    void setUp() {
        Category category = new Category(1L, "Plato Fuerte", "Deliciosos platos principales");
        validDish = new Dish(1L, "Bandeja Paisa", 25000, "Frijoles, arroz, carne molida", "https://img.com/bandeja.png", category, 1L, null);
    }

    @Test
    @DisplayName("Should successfully create dish with active status true")
    void shouldCreateDishSuccessfully() {
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(dishPersistentPort.saveDish(validDish)).thenReturn(validDish);

        Dish result = dishUseCase.createDish(validDish);

        assertNotNull(result);
        assertTrue(result.getActive());
        verify(restaurantPersistentPort).existsById(1L);
        verify(dishPersistentPort).saveDish(validDish);
    }

    @Test
    @DisplayName("Should throw exception when price is null")
    void shouldThrowWhenPriceIsNull() {
        validDish.setPrice(null);

        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The dish price must be an integer greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when price is zero or negative")
    void shouldThrowWhenPriceIsZeroOrNegative() {
        validDish.setPrice(0);

        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The dish price must be an integer greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when category is null")
    void shouldThrowWhenCategoryIsNull() {
        validDish.setCategory(null);

        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The dish must be assigned to a valid category", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when restaurant does not exist")
    void shouldThrowWhenRestaurantDoesNotExist() {
        when(restaurantPersistentPort.existsById(1L)).thenReturn(false);

        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The restaurant associated with the dish does not exist", exception.getMessage());
        verify(dishPersistentPort, never()).saveDish(any());
    }
}
