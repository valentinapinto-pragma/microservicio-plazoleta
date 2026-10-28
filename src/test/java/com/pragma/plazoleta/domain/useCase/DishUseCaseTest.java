package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidDishDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotOwnerOfRestaurantException;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
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
    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        Category category = new Category(1L, "Plato Fuerte", "Deliciosos platos principales");
        validDish = new Dish(1L, "Bandeja Paisa", 25000, "Frijoles, arroz, carne molida", "https://img.com/bandeja.png", category, 1L, true);
        validRestaurant = new Restaurant(1L, "El Buen Sabor", "123456789", "Calle 10", "+573001234567", "https://img.com/logo.png", 2L);
    }

    @Test
    @DisplayName("Should successfully create dish with active status true")
    void shouldCreateDishSuccessfully() {
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(dishPersistentPort.save(validDish)).thenReturn(validDish);

        Dish result = dishUseCase.createDish(validDish);

        assertNotNull(result);
        assertTrue(result.getActive());
        verify(restaurantPersistentPort).existsById(1L);
        verify(dishPersistentPort).save(validDish);
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
        verify(dishPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully update dish price and description")
    void shouldUpdateDishSuccessfully() {
        Integer newPrice = 30000;
        String newDescription = "Bandeja paisa extra grande con chicharron crocante";

        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(dishPersistentPort.save(validDish)).thenReturn(validDish);

        Dish result = dishUseCase.updateDish(1L, newPrice, newDescription);

        assertNotNull(result);
        assertEquals(newPrice, result.getPrice());
        assertEquals(newDescription, result.getDescription());
        verify(dishPersistentPort).findById(1L);
        verify(dishPersistentPort).save(validDish);
    }

    @Test
    @DisplayName("Should successfully update only description when price is null")
    void shouldUpdateOnlyDescriptionSuccessfully() {
        String newDescription = "Bandeja paisa extra grande con chicharron crocante";

        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(dishPersistentPort.save(validDish)).thenReturn(validDish);

        Dish result = dishUseCase.updateDish(1L, null, newDescription);

        assertNotNull(result);
        assertEquals(25000, result.getPrice()); // El precio se mantuvo original
        assertEquals(newDescription, result.getDescription());
        verify(dishPersistentPort).findById(1L);
        verify(dishPersistentPort).save(validDish);
    }

    @Test
    @DisplayName("Should successfully update only price when description is null")
    void shouldUpdateOnlyPriceSuccessfully() {
        Integer newPrice = 30000;
        String originalDescription = validDish.getDescription();

        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(dishPersistentPort.save(validDish)).thenReturn(validDish);

        Dish result = dishUseCase.updateDish(1L, newPrice, null);

        assertNotNull(result);
        assertEquals(newPrice, result.getPrice());
        assertEquals(originalDescription, result.getDescription()); // La descripción se mantuvo original
        verify(dishPersistentPort).findById(1L);
        verify(dishPersistentPort).save(validDish);
    }

    @Test
    @DisplayName("Should throw exception when both price and description are null")
    void shouldThrowWhenBothPriceAndDescriptionAreNull() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.updateDish(1L, null, null)
        );
        assertEquals("At least one field must be provided for update", exception.getMessage());
        verify(dishPersistentPort, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when update price is zero or negative")
    void shouldThrowWhenUpdatePriceIsZeroOrNegative() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.updateDish(1L, -1000, "Nueva descripcion")
        );
        assertEquals("The dish price must be an integer greater than 0", exception.getMessage());
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
                () -> dishUseCase.updateDish(99L, 30000, "Nueva descripcion")
        );
        assertEquals("Dish not found with id: 99", exception.getMessage());
        verify(dishPersistentPort).findById(99L);
        verify(dishPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully disable dish when user is the restaurant owner")
    void shouldDisableDishSuccessfullyWhenUserIsOwner() {
        Long ownerId = 2L;
        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.of(validRestaurant));
        when(dishPersistentPort.save(validDish)).thenReturn(validDish);

        Dish result = dishUseCase.changeDishStatus(1L, false, ownerId);

        assertNotNull(result);
        assertFalse(result.getActive());
        verify(dishPersistentPort).findById(1L);
        verify(restaurantPersistentPort).findById(1L);
        verify(dishPersistentPort).save(validDish);
    }

    @Test
    @DisplayName("Should successfully enable dish when user is the restaurant owner")
    void shouldEnableDishSuccessfullyWhenUserIsOwner() {
        Long ownerId = 2L;
        validDish.setActive(false);

        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.of(validRestaurant));
        when(dishPersistentPort.save(validDish)).thenReturn(validDish);

        Dish result = dishUseCase.changeDishStatus(1L, true, ownerId);

        assertNotNull(result);
        assertTrue(result.getActive());
        verify(dishPersistentPort).save(validDish);
    }

    @Test
    @DisplayName("Should throw exception when active status is null")
    void shouldThrowWhenActiveStatusIsNull() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.changeDishStatus(1L, null, 2L)
        );
        assertEquals("The active status cannot be null", exception.getMessage());
        verify(dishPersistentPort, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when dish to change status is not found")
    void shouldThrowWhenDishNotFoundOnStatusChange() {
        when(dishPersistentPort.findById(99L)).thenReturn(Optional.empty());

        DishNotFoundException exception = assertThrows(
                DishNotFoundException.class,
                () -> dishUseCase.changeDishStatus(99L, false, 2L)
        );
        assertEquals("Dish not found with id: 99", exception.getMessage());
        verify(restaurantPersistentPort, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when restaurant of dish is not found")
    void shouldThrowWhenRestaurantNotFoundOnStatusChange() {
        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.empty());

        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> dishUseCase.changeDishStatus(1L, false, 2L)
        );
        assertEquals("The restaurant associated with the dish does not exist", exception.getMessage());
        verify(dishPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserNotOwnerOfRestaurantException when caller is not the restaurant owner")
    void shouldThrowWhenUserIsNotRestaurantOwner() {
        Long impostorOwnerId = 999L;

        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.of(validRestaurant));

        UserNotOwnerOfRestaurantException exception = assertThrows(
                UserNotOwnerOfRestaurantException.class,
                () -> dishUseCase.changeDishStatus(1L, false, impostorOwnerId)
        );
        assertEquals("The user is not the owner of the restaurant to which the dish belongs", exception.getMessage());
        verify(dishPersistentPort, never()).save(any());
    }
}
