package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.DishDoesNotBelongToRestaurantException;
import com.pragma.plazoleta.domain.exception.DishInactiveException;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidOrderDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.OrderPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock
    private OrderPersistentPort orderPersistentPort;

    @Mock
    private DishPersistentPort dishPersistentPort;

    @Mock
    private RestaurantPersistentPort restaurantPersistentPort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private Order validOrder;
    private Dish validDish1;
    private Dish validDish2;

    @BeforeEach
    void setUp() {
        Category category = new Category(1L, "Comida Rápida", "Platos rápidos");

        validDish1 = new Dish(100L, "Hamburguesa", 25000, "Rica hamburguesa", "http://img.com/1", category, 1L, true);
        validDish2 = new Dish(101L, "Papas Fritas", 10000, "Papas crocantes", "http://img.com/2", category, 1L, true);

        List<OrderDish> dishes = new ArrayList<>();
        dishes.add(new OrderDish(null, null, 100L, 2));
        dishes.add(new OrderDish(null, null, 101L, 1));

        validOrder = new Order(
                null,
                10L,
                null,
                null,
                null,
                1L,
                dishes
        );
    }

    @Test
    @DisplayName("Should successfully create order when all data and business rules are valid")
    void shouldCreateOrderSuccessfully() {
        // Arrange
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(validDish1));
        when(dishPersistentPort.findById(101L)).thenReturn(Optional.of(validDish2));
        when(orderPersistentPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderUseCase.createOrder(validOrder);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.PENDIENTE, result.getStatus());
        assertNotNull(result.getDate());
        assertNull(result.getChefId());
        verify(restaurantPersistentPort, times(1)).existsById(1L);
        verify(orderPersistentPort, times(1)).existsByClientIdAndStatusIn(eq(10L), anyList());
        verify(orderPersistentPort, times(1)).save(validOrder);
    }

    @Test
    @DisplayName("Should throw exception when order dishes list is null")
    void shouldThrowExceptionWhenOrderDishesIsNull() {
        // Arrange
        validOrder.setOrderDishes(null);

        // Act & Assert
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The order must contain at least one dish", exception.getMessage());
        verifyNoInteractions(restaurantPersistentPort, orderPersistentPort, dishPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when order dishes list is empty")
    void shouldThrowExceptionWhenOrderDishesIsEmpty() {
        // Arrange
        validOrder.setOrderDishes(Collections.emptyList());

        // Act & Assert
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The order must contain at least one dish", exception.getMessage());
        verifyNoInteractions(restaurantPersistentPort, orderPersistentPort, dishPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when client id is null")
    void shouldThrowExceptionWhenClientIdIsNull() {
        // Arrange
        validOrder.setClientId(null);

        // Act & Assert
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("Client id cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when order contains duplicate dishes")
    void shouldThrowExceptionWhenOrderContainsDuplicateDishes() {
        // Arrange
        validOrder.setOrderDishes(List.of(
                new OrderDish(null, null, 100L, 2),
                new OrderDish(null, null, 100L, 1)
        ));

        // Act & Assert
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The order cannot contain duplicate dishes; please specify the total quantity", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when restaurant does not exist")
    void shouldThrowExceptionWhenRestaurantDoesNotExist() {
        // Arrange
        when(restaurantPersistentPort.existsById(1L)).thenReturn(false);

        // Act & Assert
        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("Restaurant not found with id: 1", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when client already has an active order in progress")
    void shouldThrowExceptionWhenClientHasActiveOrder() {
        // Arrange
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(true);

        // Act & Assert
        ClientHasActiveOrderException exception = assertThrows(
                ClientHasActiveOrderException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The client already has an active order in progress", exception.getMessage());
        verify(dishPersistentPort, never()).findById(any());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when dish quantity is zero or negative")
    void shouldThrowExceptionWhenDishQuantityIsInvalid() {
        // Arrange
        validOrder.getOrderDishes().get(0).setQuantity(0);

        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);

        // Act & Assert
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The dish quantity must be greater than zero", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when dish does not exist")
    void shouldThrowExceptionWhenDishDoesNotExist() {
        // Arrange
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.empty());

        // Act & Assert
        DishNotFoundException exception = assertThrows(
                DishNotFoundException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("Dish not found with id: 100", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when dish belongs to a different restaurant")
    void shouldThrowExceptionWhenDishBelongsToDifferentRestaurant() {
        // Arrange: validDish1 belongs to restaurant 999L instead of 1L
        Dish dishFromOtherRestaurant = new Dish(100L, "Sushi", 30000, "Sushi roll", "http://img.com/3", null, 999L, true);

        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(dishFromOtherRestaurant));

        // Act & Assert
        DishDoesNotBelongToRestaurantException exception = assertThrows(
                DishDoesNotBelongToRestaurantException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The dish with id 100 does not belong to restaurant with id 1", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when dish is inactive")
    void shouldThrowExceptionWhenDishIsInactive() {
        // Arrange: validDish1 is inactive (active = false)
        Dish inactiveDish = new Dish(100L, "Hamburguesa", 25000, "Rica", "http://img.com/1", null, 1L, false);

        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(inactiveDish));

        // Act & Assert
        DishInactiveException exception = assertThrows(
                DishInactiveException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The dish 'Hamburguesa' is currently inactive", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }
}