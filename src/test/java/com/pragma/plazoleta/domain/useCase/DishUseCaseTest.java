package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidDishDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotOwnerOfRestaurantException;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserInformationPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishUseCaseTest {

    @Mock
    private DishPersistentPort dishPersistentPort;

    @Mock
    private RestaurantPersistentPort restaurantPersistentPort;

    @Mock
    private UserInformationPort userInformationPort;

    @InjectMocks
    private DishUseCase dishUseCase;

    private Dish validDish;
    private Restaurant validRestaurant;
    private Category validCategory;

    @BeforeEach
    void setUp() {
        validCategory = new Category(1L, "Platos Fuertes", "Comidas principales");
        validRestaurant = new Restaurant(1L, "Restaurante Central", "123456789", "Calle 10", "+573001234567", "https://logo.png", 2L);
        validDish = new Dish(1L, "Bandeja Paisa", 25000, "Deliciosa bandeja tradicional", "https://imagen.com/plato.png", validCategory, 1L, true);
    }

    @Test
    @DisplayName("Should successfully create a dish when all data is valid and user is restaurant owner")
    void shouldCreateDishSuccessfully() {
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.of(validRestaurant));
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(2L);
        when(dishPersistentPort.save(any(Dish.class))).thenReturn(validDish);

        Dish result = dishUseCase.createDish(validDish);

        assertNotNull(result);
        assertEquals("Bandeja Paisa", result.getName());
        assertTrue(result.getActive());
        verify(restaurantPersistentPort).findById(1L);
        verify(userInformationPort).getAuthenticatedUserId();
        verify(dishPersistentPort).save(validDish);
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
        validDish.setPrice(-100);

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
    @DisplayName("Should throw exception when category ID is null")
    void shouldThrowWhenCategoryIdIsNull() {
        validDish.setCategory(new Category(null, "Comida", "Desc"));

        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The dish must be assigned to a valid category", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when restaurant of the dish does not exist")
    void shouldThrowWhenRestaurantNotFoundOnCreate() {
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.empty());

        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The restaurant associated with the dish does not exist", exception.getMessage());
        verify(dishPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user is not the owner of the restaurant")
    void shouldThrowWhenUserIsNotOwnerOnCreate() {
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.of(validRestaurant));
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(999L);

        UserNotOwnerOfRestaurantException exception = assertThrows(
                UserNotOwnerOfRestaurantException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The user is not the owner of the restaurant to which the dish belongs", exception.getMessage());
        verify(dishPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully update dish price and description")
    void shouldUpdateDishPriceAndDescriptionSuccessfully() {
        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.of(validRestaurant));
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(2L);
        when(dishPersistentPort.save(any(Dish.class))).thenReturn(validDish);

        Dish result = dishUseCase.updateDish(1L, 30000, "Nueva descripcion actualizada");

        assertNotNull(result);
        verify(dishPersistentPort).save(validDish);
        assertEquals(30000, validDish.getPrice());
        assertEquals("Nueva descripcion actualizada", validDish.getDescription());
    }

    @Test
    @DisplayName("Should successfully update only dish price")
    void shouldUpdateOnlyDishPriceSuccessfully() {
        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.of(validRestaurant));
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(2L);
        when(dishPersistentPort.save(any(Dish.class))).thenReturn(validDish);

        Dish result = dishUseCase.updateDish(1L, 35000, null);

        assertNotNull(result);
        assertEquals(35000, validDish.getPrice());
        verify(dishPersistentPort).save(validDish);
    }

    @Test
    @DisplayName("Should successfully update only dish description")
    void shouldUpdateOnlyDishDescriptionSuccessfully() {
        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.of(validRestaurant));
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(2L);
        when(dishPersistentPort.save(any(Dish.class))).thenReturn(validDish);

        Dish result = dishUseCase.updateDish(1L, null, "Solo nueva descripcion");

        assertNotNull(result);
        assertEquals("Solo nueva descripcion", validDish.getDescription());
        verify(dishPersistentPort).save(validDish);
    }

    @Test
    @DisplayName("Should throw exception when updating dish with no fields provided")
    void shouldThrowWhenNoFieldsProvidedForUpdate() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.updateDish(1L, null, null)
        );
        assertEquals("At least one field must be provided for update", exception.getMessage());
        verify(dishPersistentPort, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when updating price with zero or negative")
    void shouldThrowWhenUpdatePriceIsZeroOrNegative() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.updateDish(1L, 0, "Valida")
        );
        assertEquals("The dish price must be an integer greater than 0", exception.getMessage());
        verify(dishPersistentPort, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when updating description with empty string")
    void shouldThrowWhenUpdateDescriptionIsEmpty() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.updateDish(1L, 30000, "  ")
        );
        assertEquals("The dish description cannot be empty", exception.getMessage());
        verify(dishPersistentPort, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when dish to update is not found")
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
        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.of(validRestaurant));
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(2L);
        when(dishPersistentPort.save(validDish)).thenReturn(validDish);

        Dish result = dishUseCase.changeDishStatus(1L, false);

        assertNotNull(result);
        assertFalse(result.getActive());
        verify(userInformationPort).getAuthenticatedUserId();
        verify(dishPersistentPort).findById(1L);
        verify(restaurantPersistentPort).findById(1L);
        verify(dishPersistentPort).save(validDish);
    }

    @Test
    @DisplayName("Should successfully enable dish when user is the restaurant owner")
    void shouldEnableDishSuccessfullyWhenUserIsOwner() {
        validDish.setActive(false);

        when(dishPersistentPort.findById(1L)).thenReturn(Optional.of(validDish));
        when(restaurantPersistentPort.findById(1L)).thenReturn(Optional.of(validRestaurant));
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(2L);
        when(dishPersistentPort.save(validDish)).thenReturn(validDish);

        Dish result = dishUseCase.changeDishStatus(1L, true);

        assertNotNull(result);
        assertTrue(result.getActive());
        verify(userInformationPort).getAuthenticatedUserId();
        verify(dishPersistentPort).save(validDish);
    }

    @Test
    @DisplayName("Should throw exception when active status is null")
    void shouldThrowWhenActiveStatusIsNull() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.changeDishStatus(1L, null)
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
                () -> dishUseCase.changeDishStatus(99L, false)
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
                () -> dishUseCase.changeDishStatus(1L, false)
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
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(impostorOwnerId);

        UserNotOwnerOfRestaurantException exception = assertThrows(
                UserNotOwnerOfRestaurantException.class,
                () -> dishUseCase.changeDishStatus(1L, false)
        );
        assertEquals("The user is not the owner of the restaurant to which the dish belongs", exception.getMessage());
        verify(dishPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully get dishes by restaurant when restaurant exists and valid pagination")
    void shouldGetDishesByRestaurantSuccessfully() {
        CustomPage<Dish> mockPage = new CustomPage<>(List.of(validDish), 0, 10, 1L, 1);
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(dishPersistentPort.getDishesByRestaurant(1L, null, 0, 10)).thenReturn(mockPage);

        CustomPage<Dish> result = dishUseCase.getDishesByRestaurant(1L, null, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Bandeja Paisa", result.getContent().get(0).getName());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(restaurantPersistentPort).existsById(1L);
        verify(dishPersistentPort).getDishesByRestaurant(1L, null, 0, 10);
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when restaurant does not exist on get dishes")
    void shouldThrowRestaurantNotFoundOnGetDishes() {
        when(restaurantPersistentPort.existsById(99L)).thenReturn(false);

        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> dishUseCase.getDishesByRestaurant(99L, null, 0, 10)
        );

        assertEquals("The restaurant associated with the dish does not exist", exception.getMessage());
        verify(dishPersistentPort, never()).getDishesByRestaurant(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw InvalidDishDataException when page is negative")
    void shouldThrowInvalidDataWhenPageIsNegative() {
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);

        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.getDishesByRestaurant(1L, null, -1, 10)
        );

        assertEquals("The page number must be greater than or equal to 0", exception.getMessage());
        verify(dishPersistentPort, never()).getDishesByRestaurant(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw InvalidDishDataException when size is zero or negative")
    void shouldThrowInvalidDataWhenSizeIsZeroOrNegative() {
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);

        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.getDishesByRestaurant(1L, null, 0, 0)
        );

        assertEquals("The page size must be greater than 0", exception.getMessage());
        verify(dishPersistentPort, never()).getDishesByRestaurant(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when dish restaurant id is null on create")
    void shouldThrowWhenDishRestaurantIdIsNull() {
        validDish.setIdRestaurant(null);

        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> dishUseCase.createDish(validDish)
        );
        assertEquals("The restaurant associated with the dish does not exist", exception.getMessage());
        verify(dishPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating dish with null price and blank description")
    void shouldThrowWhenUpdatingWithNullPriceAndBlankDescription() {
        InvalidDishDataException exception = assertThrows(
                InvalidDishDataException.class,
                () -> dishUseCase.updateDish(1L, null, "   ")
        );
        assertEquals("At least one field must be provided for update", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw RestaurantNotFoundException when idRestaurant is null on getDishesByRestaurant")
    void shouldThrowWhenIdRestaurantIsNullOnGetDishes() {
        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> dishUseCase.getDishesByRestaurant(null, null, 0, 10)
        );
        assertEquals("The restaurant associated with the dish does not exist", exception.getMessage());
    }
}