package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.InvalidRestaurantDataException;
import com.pragma.plazoleta.domain.exception.OwnerNotValidException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserValidationPort;
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
class RestaurantUseCaseTest {

    @Mock
    private RestaurantPersistentPort restaurantPersistentPort;

    @Mock
    private UserValidationPort userValidationPort;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    private Restaurant validRestaurant;

    @BeforeEach
    void setUp() {
        validRestaurant = new Restaurant(
                1L,
                "Restaurante La 80",
                "123456789",
                "Calle 10 # 5-20",
                "+573001234567",
                "https://imagen.com/logo.png",
                2L
        );
    }

    @Test
    @DisplayName("Should successfully create restaurant when data is valid and owner exists")
    void shouldCreateRestaurantWhenDataIsValid() {
        // Arrange
        when(userValidationPort.isValidOwner(2L)).thenReturn(true);
        when(restaurantPersistentPort.existsByNit("123456789")).thenReturn(false);
        when(restaurantPersistentPort.saveRestaurant(validRestaurant)).thenReturn(validRestaurant);

        // Act
        Restaurant result = restaurantUseCase.createRestaurant(validRestaurant);

        // Assert
        assertNotNull(result);
        assertEquals(validRestaurant.getName(), result.getName());
        verify(userValidationPort).isValidOwner(2L);
        verify(restaurantPersistentPort).existsByNit("123456789");
        verify(restaurantPersistentPort).saveRestaurant(validRestaurant);
    }

    @Test
    @DisplayName("Should successfully create restaurant when phone has no plus sign")
    void shouldCreateRestaurantWhenPhoneHasNoPlusSign() {
        // Arrange
        validRestaurant.setPhone("573001234567");
        when(userValidationPort.isValidOwner(2L)).thenReturn(true);
        when(restaurantPersistentPort.existsByNit("123456789")).thenReturn(false);
        when(restaurantPersistentPort.saveRestaurant(validRestaurant)).thenReturn(validRestaurant);

        // Act
        Restaurant result = restaurantUseCase.createRestaurant(validRestaurant);

        // Assert
        assertNotNull(result);
        assertEquals("573001234567", result.getPhone());
    }

    @Test
    @DisplayName("Should throw exception when restaurant name is null")
    void shouldThrowWhenNameIsNull() {
        // Arrange
        validRestaurant.setName(null);

        // Act & Assert
        InvalidRestaurantDataException exception = assertThrows(
                InvalidRestaurantDataException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );
        assertEquals("The restaurant name cannot consist solely of numbers", exception.getMessage());
        verifyNoInteractions(userValidationPort, restaurantPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when restaurant name consists solely of numbers")
    void shouldThrowWhenNameIsOnlyNumbers() {
        // Arrange
        validRestaurant.setName("1234567");

        // Act & Assert
        InvalidRestaurantDataException exception = assertThrows(
                InvalidRestaurantDataException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );
        assertEquals("The restaurant name cannot consist solely of numbers", exception.getMessage());
        verifyNoInteractions(userValidationPort, restaurantPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when NIT is null")
    void shouldThrowWhenNitIsNull() {
        // Arrange
        validRestaurant.setNit(null);

        // Act & Assert
        InvalidRestaurantDataException exception = assertThrows(
                InvalidRestaurantDataException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );
        assertEquals("The NIT must be strictly numeric", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when NIT contains non-numeric characters")
    void shouldThrowWhenNitContainsLetters() {
        // Arrange
        validRestaurant.setNit("12345ABC");

        // Act & Assert
        InvalidRestaurantDataException exception = assertThrows(
                InvalidRestaurantDataException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );
        assertEquals("The NIT must be strictly numeric", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when phone is null")
    void shouldThrowWhenPhoneIsNull() {
        // Arrange
        validRestaurant.setPhone(null);

        // Act & Assert
        InvalidRestaurantDataException exception = assertThrows(
                InvalidRestaurantDataException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );
        assertEquals("The phone number must be numeric, max 13 characters, and may start with '+'", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when phone contains invalid characters")
    void shouldThrowWhenPhoneContainsLetters() {
        // Arrange
        validRestaurant.setPhone("+57ABC300");

        // Act & Assert
        InvalidRestaurantDataException exception = assertThrows(
                InvalidRestaurantDataException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );
        assertEquals("The phone number must be numeric, max 13 characters, and may start with '+'", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when phone exceeds 13 characters")
    void shouldThrowWhenPhoneExceeds13Digits() {
        // Arrange
        validRestaurant.setPhone("+5730012345678901");

        // Act & Assert
        InvalidRestaurantDataException exception = assertThrows(
                InvalidRestaurantDataException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );
        assertEquals("The phone number must be numeric, max 13 characters, and may start with '+'", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when owner ID is null")
    void shouldThrowWhenOwnerIdIsNull() {
        // Arrange
        validRestaurant.setIdOwner(null);

        // Act & Assert
        OwnerNotValidException exception = assertThrows(
                OwnerNotValidException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );
        assertEquals("The user specified as owner does not exist or does not possess the OWNER role", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when owner validation returns false")
    void shouldThrowWhenOwnerIsNotValid() {
        // Arrange
        when(userValidationPort.isValidOwner(2L)).thenReturn(false);

        // Act & Assert
        OwnerNotValidException exception = assertThrows(
                OwnerNotValidException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );
        assertEquals("The user specified as owner does not exist or does not possess the OWNER role", exception.getMessage());
        verify(userValidationPort).isValidOwner(2L);
        verify(restaurantPersistentPort, never()).saveRestaurant(any());
    }

    @Test
    @DisplayName("Should throw exception when NIT already exists")
    void shouldThrowWhenNitAlreadyExists() {
        // Arrange
        when(userValidationPort.isValidOwner(2L)).thenReturn(true);
        when(restaurantPersistentPort.existsByNit("123456789")).thenReturn(true);

        // Act & Assert
        InvalidRestaurantDataException exception = assertThrows(
                InvalidRestaurantDataException.class,
                () -> restaurantUseCase.createRestaurant(validRestaurant)
        );
        assertEquals("A restaurant with the provided NIT already exists", exception.getMessage());
        verify(restaurantPersistentPort, never()).saveRestaurant(any());
    }
}
