package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.DishNotFoundException;
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

import java.util.Optional;

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
        validDish = new Dish(1L, "Bandeja Paisa", 25000, "Frijoles, arroz, carne molida", "https://img.com/bandeja.png", category, 1L, true);
    }

    // --- TESTS CREATE DISH (HU-3) ---

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
    @DisplayName("Should throw exception when price is null on create")
    void shouldThrowWhenPriceIsNull() {
        validDish.setPrice(null);

        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The dish price must be an integer greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when price is zero or negative on create")
    void shouldThrowWhenPriceIsZeroOrNegative() {
        validDish.setPrice(0);

        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The dish price must be an integer greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when category is null on create")
    void shouldThrowWhenCategoryIsNull() {
        validDish.setCategory(null);

        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The dish must be assigned to a valid category", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when restaurant does not exist on create")
    void shouldThrowWhenRestaurantDoesNotExist() {
        when(restaurantPersistentPort.existsById(1L)).thenReturn(false);

        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The restaurant associated with the dish does not exist", exception.getMessage());
        verify(dishPersistentPort, never()).saveDish(any());
    }

    // --- TESTS UPDATE DISH (HU-4) ---

    @Test
    @DisplayName("Should successfully update dish price and description")
    void shouldUpdateDishSuccessfully() {
        // Arrange
        Integer newPrice = 30000;
        String newDescription = "Bandeja paisa extra grande con chicharrón crocante";

        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(dishPersistentPort.saveDish(validDish)).thenReturn(validDish);

        // Act
        Dish result = dishUseCase.updateDish(1L, newPrice, newDescription);

        // Assert
        assertNotNull(result);
        assertEquals(newPrice, result.getPrice());
        assertEquals(newDescription, result.getDescription());
        verify(dishPersistentPort).findById(1L);
        verify(dishPersistentPort).saveDish(validDish);
    }

    @Test
    @DisplayName("Should throw exception when update price is null")
    void shouldThrowWhenUpdatePriceIsNull() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.updateDish(1L, null, "Nueva descripción")
        );
        assertEquals("The dish price must be an integer greater than 0", exception.getMessage());
        verify(dishPersistentPort, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when update price is zero or negative")
    void shouldThrowWhenUpdatePriceIsZeroOrNegative() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.updateDish(1L, -1000, "Nueva descripción")
        );
        assertEquals("The dish price must be an integer greater than 0", exception.getMessage());
        verify(dishPersistentPort, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when update description is null")
    void shouldThrowWhenUpdateDescriptionIsNull() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.updateDish(1L, 25000, null)
        );
        assertEquals("The dish description cannot be empty", exception.getMessage());
        verify(dishPersistentPort, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when update description is blank")
    void shouldThrowWhenUpdateDescriptionIsBlank() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.updateDish(1L, 25000, "    ")
        );
        assertEquals("The dish description cannot be empty", exception.getMessage());
        verify(dishPersistentPort, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when dish to update does not exist")
    void shouldThrowWhenDishToUpdateNotFound() {
        when(dishPersistentPort.findById(99L)).thenReturn(Optional.empty());

        DishNotFoundException exception = assertThrows(
                DishNotFoundException.class,
                () -> dishUseCase.updateDish(99L, 30000, "Nueva descripción")
        );
        assertEquals("Dish not found with id: 99", exception.getMessage());
        verify(dishPersistentPort).findById(99L);
        verify(dishPersistentPort, never()).saveDish(any());
    }
}
