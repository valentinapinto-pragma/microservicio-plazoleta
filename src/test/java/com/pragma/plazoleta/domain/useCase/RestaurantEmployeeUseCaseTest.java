package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.EmployeeAlreadyAssignedException;
import com.pragma.plazoleta.domain.exception.InvalidEmployeeException;
import com.pragma.plazoleta.domain.exception.InvalidRestaurantDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotOwnerOfRestaurantException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantEmployeePersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserInformationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantEmployeeUseCaseTest {

    @Mock
    private RestaurantPersistentPort restaurantPersistentPort;

    @Mock
    private RestaurantEmployeePersistentPort restaurantEmployeePersistentPort;

    @Mock
    private UserInformationPort userInformationPort;

    @InjectMocks
    private RestaurantEmployeeUseCase restaurantEmployeeUseCase;

    private Long restaurantId;
    private Long employeeId;
    private Long ownerId;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurantId = 1L;
        employeeId = 5L;
        ownerId = 10L;
        restaurant = new Restaurant(restaurantId, "El Corral", "123456789", "Calle 10", "+573001234567", "http://logo.com", ownerId);
    }

    @Test
    @DisplayName("Should assign employee to restaurant successfully")
    void shouldAssignEmployeeToRestaurantSuccessfully() {
        when(restaurantPersistentPort.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userInformationPort.isValidEmployee(employeeId)).thenReturn(true);
        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        restaurantEmployeeUseCase.assignEmployeeToRestaurant(restaurantId, employeeId, ownerId);

        verify(restaurantPersistentPort, times(1)).findById(restaurantId);
        verify(userInformationPort, times(1)).isValidEmployee(employeeId);
        verify(restaurantEmployeePersistentPort, times(1)).findByEmployeeId(employeeId);
        verify(restaurantEmployeePersistentPort, times(1)).save(any(RestaurantEmployee.class));
    }

    @Test
    @DisplayName("Should throw exception when any ID is null")
    void shouldThrowWhenAnyIdIsNull() {
        assertThrows(InvalidRestaurantDataException.class,
                () -> restaurantEmployeeUseCase.assignEmployeeToRestaurant(null, employeeId, ownerId));

        assertThrows(InvalidRestaurantDataException.class,
                () -> restaurantEmployeeUseCase.assignEmployeeToRestaurant(restaurantId, null, ownerId));

        assertThrows(InvalidRestaurantDataException.class,
                () -> restaurantEmployeeUseCase.assignEmployeeToRestaurant(restaurantId, employeeId, null));

        verify(restaurantEmployeePersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when restaurant not found")
    void shouldThrowWhenRestaurantNotFound() {
        when(restaurantPersistentPort.findById(restaurantId)).thenReturn(Optional.empty());

        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantEmployeeUseCase.assignEmployeeToRestaurant(restaurantId, employeeId, ownerId)
        );

        assertEquals("Restaurant not found with id: " + restaurantId, exception.getMessage());
        verify(restaurantEmployeePersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user is not the owner of the restaurant")
    void shouldThrowWhenUserIsNotOwnerOfRestaurant() {
        Long differentOwnerId = 99L;
        when(restaurantPersistentPort.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        UserNotOwnerOfRestaurantException exception = assertThrows(
                UserNotOwnerOfRestaurantException.class,
                () -> restaurantEmployeeUseCase.assignEmployeeToRestaurant(restaurantId, employeeId, differentOwnerId)
        );

        assertEquals("The user is not the owner of the restaurant", exception.getMessage());
        verify(restaurantEmployeePersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user is not a valid employee")
    void shouldThrowWhenUserIsNotValidEmployee() {
        when(restaurantPersistentPort.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userInformationPort.isValidEmployee(employeeId)).thenReturn(false);

        InvalidEmployeeException exception = assertThrows(
                InvalidEmployeeException.class,
                () -> restaurantEmployeeUseCase.assignEmployeeToRestaurant(restaurantId, employeeId, ownerId)
        );

        assertEquals("The user is not a valid employee", exception.getMessage());
        verify(restaurantEmployeePersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when employee is already assigned to a restaurant")
    void shouldThrowWhenEmployeeAlreadyAssigned() {
        RestaurantEmployee existingAssignment = new RestaurantEmployee(1L, restaurantId, employeeId);
        when(restaurantPersistentPort.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userInformationPort.isValidEmployee(employeeId)).thenReturn(true);
        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.of(existingAssignment));

        EmployeeAlreadyAssignedException exception = assertThrows(
                EmployeeAlreadyAssignedException.class,
                () -> restaurantEmployeeUseCase.assignEmployeeToRestaurant(restaurantId, employeeId, ownerId)
        );

        assertEquals("The employee is already assigned to a restaurant", exception.getMessage());
        verify(restaurantEmployeePersistentPort, never()).save(any());
    }
}