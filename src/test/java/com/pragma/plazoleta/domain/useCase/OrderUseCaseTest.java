package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.*;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.repositoryPort.*;
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

    @Mock
    private RestaurantEmployeePersistentPort restaurantEmployeePersistentPort;

    @Mock
    private UserInformationPort userInformationPort;

    @Mock
    private MessageNotificationPort messageNotificationPort;

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

    // TESTS: HU-11 - createOrder

    @Test
    @DisplayName("Should successfully create order when all data and business rules are valid")
    void shouldCreateOrderSuccessfully() {
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(validDish1));
        when(dishPersistentPort.findById(101L)).thenReturn(Optional.of(validDish2));
        when(orderPersistentPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderUseCase.createOrder(validOrder);

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
        validOrder.setOrderDishes(null);

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
        validOrder.setOrderDishes(Collections.emptyList());

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
        validOrder.setClientId(null);

        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("Client id cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when order contains duplicate dishes")
    void shouldThrowExceptionWhenOrderContainsDuplicateDishes() {
        validOrder.setOrderDishes(List.of(
                new OrderDish(null, null, 100L, 2),
                new OrderDish(null, null, 100L, 1)
        ));

        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The order cannot contain duplicate dishes; please specify the total quantity", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when restaurant does not exist")
    void shouldThrowExceptionWhenRestaurantDoesNotExist() {
        when(restaurantPersistentPort.existsById(1L)).thenReturn(false);

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
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(true);

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
        validOrder.getOrderDishes().get(0).setQuantity(0);

        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);

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
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.empty());

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
        Dish dishFromOtherRestaurant = new Dish(100L, "Sushi", 30000, "Sushi roll", "http://img.com/3", null, 999L, true);

        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(dishFromOtherRestaurant));

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
        Dish inactiveDish = new Dish(100L, "Hamburguesa", 25000, "Rica", "http://img.com/1", null, 1L, false);

        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(inactiveDish));

        DishInactiveException exception = assertThrows(
                DishInactiveException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The dish 'Hamburguesa' is currently inactive", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    // TESTS: HU-12 - getOrdersByStatus

    @Test
    @DisplayName("Should successfully get orders by status enriched with dish details")
    void shouldGetOrdersByStatusSuccessfully() {
        Long employeeId = 50L;
        Long restaurantId = 1L;
        RestaurantEmployee employee = new RestaurantEmployee(1L, restaurantId, employeeId);

        Order existingOrder = new Order(
                1000L,
                10L,
                null,
                OrderStatus.PENDIENTE,
                null,
                restaurantId,
                List.of(
                        new OrderDish(1L, 1000L, 100L, 2),
                        new OrderDish(2L, 1000L, 101L, 1)
                )
        );

        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.getOrdersByRestaurantAndStatus(restaurantId, OrderStatus.PENDIENTE, 0, 10))
                .thenReturn(List.of(existingOrder));
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(validDish1));
        when(dishPersistentPort.findById(101L)).thenReturn(Optional.of(validDish2));

        // Act
        List<Order> result = orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10, employeeId);

        assertNotNull(result);
        assertEquals(1, result.size());
        Order returnedOrder = result.get(0);
        assertEquals(1000L, returnedOrder.getId());
        assertEquals(2, returnedOrder.getOrderDishes().size());
        assertEquals(validDish1, returnedOrder.getOrderDishes().get(0).getDish());
        assertEquals(validDish2, returnedOrder.getOrderDishes().get(1).getDish());

        verify(restaurantEmployeePersistentPort, times(1)).findByEmployeeId(employeeId);
        verify(orderPersistentPort, times(1)).getOrdersByRestaurantAndStatus(restaurantId, OrderStatus.PENDIENTE, 0, 10);
        verify(dishPersistentPort, times(1)).findById(100L);
        verify(dishPersistentPort, times(1)).findById(101L);
    }

    @Test
    @DisplayName("Should throw exception when employee is not assigned to any restaurant")
    void shouldThrowExceptionWhenEmployeeNotAssignedToRestaurant() {
        Long employeeId = 99L;
        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        EmployeeNotAssignedToRestaurantException exception = assertThrows(
                EmployeeNotAssignedToRestaurantException.class,
                () -> orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10, employeeId)
        );
        assertEquals("The employee is not assigned to any restaurant", exception.getMessage());
        verify(orderPersistentPort, never()).getOrdersByRestaurantAndStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw exception when status is null")
    void shouldThrowExceptionWhenStatusIsNull() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.getOrdersByStatus(null, 0, 10, 50L)
        );
        assertEquals("Order status cannot be null", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when page is negative")
    void shouldThrowExceptionWhenPageIsNegative() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, -1, 10, 50L)
        );
        assertEquals("The page must be greater than or equal to 0", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when size is zero or negative")
    void shouldThrowExceptionWhenSizeIsZeroOrNegative() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 0, 50L)
        );
        assertEquals("The page size must be greater than 0", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when employee id is null")
    void shouldThrowExceptionWhenEmployeeIdIsNull() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10, null)
        );
        assertEquals("Employee id cannot be null", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }

    @Test
    @DisplayName("Should return empty list when no orders are found for restaurant and status")
    void shouldReturnEmptyListWhenNoOrdersFound() {
        Long employeeId = 50L;
        Long restaurantId = 1L;
        RestaurantEmployee employee = new RestaurantEmployee(1L, restaurantId, employeeId);

        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.getOrdersByRestaurantAndStatus(restaurantId, OrderStatus.PENDIENTE, 0, 10))
                .thenReturn(Collections.emptyList());

        // Act
        List<Order> result = orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10, employeeId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(dishPersistentPort);
    }

    @Test
    @DisplayName("Should handle order with null dishes list gracefully")
    void shouldHandleOrderWithNullDishesGracefully() {
        Long employeeId = 50L;
        Long restaurantId = 1L;
        RestaurantEmployee employee = new RestaurantEmployee(1L, restaurantId, employeeId);

        Order orderWithNullDishes = new Order(1000L, 10L, null, OrderStatus.PENDIENTE, null, restaurantId, null);

        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.getOrdersByRestaurantAndStatus(restaurantId, OrderStatus.PENDIENTE, 0, 10))
                .thenReturn(List.of(orderWithNullDishes));

        // Act
        List<Order> result = orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10, employeeId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getOrderDishes());
        verifyNoInteractions(dishPersistentPort);
    }

    // TESTS: HU-13 - assignOrder
    @Test
    @DisplayName("Should successfully assign order to employee and change status to EN_PREPARACION")
    void shouldAssignOrderSuccessfully() {
        // Arrange
        Long orderId = 100L;
        Long employeeId = 3L;
        Long restaurantId = 1L;
        RestaurantEmployee employee = new RestaurantEmployee(1L, restaurantId, employeeId);
        Order existingOrder = new Order(
                orderId,
                10L,
                null,
                OrderStatus.PENDIENTE,
                null,
                restaurantId,
                List.of(new OrderDish(1L, orderId, 100L, 2))
        );
        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderPersistentPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(validDish1));
        // Act
        Order result = orderUseCase.assignOrder(orderId, OrderStatus.EN_PREPARACION, employeeId);
        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.EN_PREPARACION, result.getStatus());
        assertEquals(employeeId, result.getChefId());
        assertEquals(validDish1, result.getOrderDishes().get(0).getDish());
        verify(restaurantEmployeePersistentPort, times(1)).findByEmployeeId(employeeId);
        verify(orderPersistentPort, times(1)).findById(orderId);
        verify(orderPersistentPort, times(1)).save(existingOrder);
        verify(dishPersistentPort, times(1)).findById(100L);
    }
    @Test
    @DisplayName("Should throw exception when order id is null on assign")
    void shouldThrowWhenOrderIdIsNullOnAssign() {
        // Act & Assert
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.assignOrder(null, OrderStatus.EN_PREPARACION, 3L)
        );
        assertEquals("Order id cannot be null", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }
    @Test
    @DisplayName("Should throw exception when status is null on assign")
    void shouldThrowWhenStatusIsNullOnAssign() {
        // Act & Assert
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.assignOrder(100L, null, 3L)
        );
        assertEquals("Order status cannot be null", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }
    @Test
    @DisplayName("Should throw exception when employee id is null on assign")
    void shouldThrowWhenEmployeeIdIsNullOnAssign() {
        // Act & Assert
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.assignOrder(100L, OrderStatus.EN_PREPARACION, null)
        );
        assertEquals("Employee id cannot be null", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }
    @Test
    @DisplayName("Should throw exception when requested status is not EN_PREPARACION on assign")
    void shouldThrowWhenStatusIsNotInPreparationOnAssign() {
        // Act & Assert
        InvalidOrderStatusException exception = assertThrows(
                InvalidOrderStatusException.class,
                () -> orderUseCase.assignOrder(100L, OrderStatus.LISTO, 3L)
        );
        assertEquals("The requested status transition is invalid; only EN_PREPARACION is allowed for order assignment", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }
    @Test
    @DisplayName("Should throw exception when employee is not assigned to any restaurant on assign")
    void shouldThrowWhenEmployeeNotAssignedToRestaurantOnAssign() {
        // Arrange
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.empty());
        // Act & Assert
        EmployeeNotAssignedToRestaurantException exception = assertThrows(
                EmployeeNotAssignedToRestaurantException.class,
                () -> orderUseCase.assignOrder(100L, OrderStatus.EN_PREPARACION, 3L)
        );
        assertEquals("The employee is not assigned to any restaurant", exception.getMessage());
        verify(orderPersistentPort, never()).findById(any());
        verify(orderPersistentPort, never()).save(any());
    }
    @Test
    @DisplayName("Should throw exception when order does not exist on assign")
    void shouldThrowWhenOrderNotFoundOnAssign() {
        // Arrange
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(999L)).thenReturn(Optional.empty());
        // Act & Assert
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderUseCase.assignOrder(999L, OrderStatus.EN_PREPARACION, 3L)
        );
        assertEquals("Order not found with id: 999", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }
    @Test
    @DisplayName("Should throw exception when order belongs to a different restaurant on assign")
    void shouldThrowWhenOrderDoesNotBelongToEmployeeRestaurantOnAssign() {
        // Arrange: Employee belongs to restaurant 1, but order belongs to restaurant 2
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        Order orderFromOtherRestaurant = new Order(100L, 10L, null, OrderStatus.PENDIENTE, null, 2L, List.of());
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(100L)).thenReturn(Optional.of(orderFromOtherRestaurant));
        // Act & Assert
        OrderDoesNotBelongToRestaurantException exception = assertThrows(
                OrderDoesNotBelongToRestaurantException.class,
                () -> orderUseCase.assignOrder(100L, OrderStatus.EN_PREPARACION, 3L)
        );
        assertEquals("The order does not belong to the restaurant where the employee works", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }
    @Test
    @DisplayName("Should throw exception when order is not in PENDIENTE status on assign")
    void shouldThrowWhenOrderIsNotInPendingStatusOnAssign() {
        // Arrange: Order is already in EN_PREPARACION
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        Order orderAlreadyInPreparation = new Order(100L, 10L, null, OrderStatus.EN_PREPARACION, 2L, 1L, List.of());
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(100L)).thenReturn(Optional.of(orderAlreadyInPreparation));
        // Act & Assert
        InvalidOrderStatusException exception = assertThrows(
                InvalidOrderStatusException.class,
                () -> orderUseCase.assignOrder(100L, OrderStatus.EN_PREPARACION, 3L)
        );
        assertEquals("Only orders in PENDIENTE status can be assigned to a chef", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }
    @Test
    @DisplayName("Should throw exception when restaurant id is null on create order")
    void shouldThrowExceptionWhenRestaurantIdIsNull() {
        validOrder.setRestaurantId(null);

        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("Restaurant not found with id: null", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when dish quantity is null")
    void shouldThrowExceptionWhenDishQuantityIsNull() {
        validOrder.getOrderDishes().get(0).setQuantity(null);

        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);

        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The dish quantity must be greater than zero", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    // TESTS: HU-14 - updateOrderToReady

    @Test
    @DisplayName("Should successfully update order to ready, generate PIN, notify client, and enrich dishes")
    void shouldUpdateOrderToReadySuccessfully() {
        Long orderId = 100L;
        Long employeeId = 3L;
        Long restaurantId = 1L;
        Long clientId = 10L;

        RestaurantEmployee employee = new RestaurantEmployee(1L, restaurantId, employeeId);
        List<OrderDish> dishes = new ArrayList<>();
        dishes.add(new OrderDish(1L, null, 100L, 2));

        Order existingOrder = new Order(orderId, clientId, null, OrderStatus.EN_PREPARACION, employeeId, restaurantId, dishes);

        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderPersistentPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userInformationPort.getClientPhoneNumber(clientId)).thenReturn("+573001234567");
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(validDish1));

        // Act
        Order result = orderUseCase.updateOrderToReady(orderId, employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.LISTO, result.getStatus());
        assertNotNull(result.getSecurityPin());
        assertTrue(result.getSecurityPin().matches("\\d{6}")); // Valida que sean 6 dígitos numéricos
        verify(orderPersistentPort, times(1)).save(existingOrder);
        verify(userInformationPort, times(1)).getClientPhoneNumber(clientId);
        verify(messageNotificationPort, times(1)).sendMessage(eq("+573001234567"), contains(result.getSecurityPin()));
        assertNotNull(result.getOrderDishes().get(0).getDish());
    }

    @Test
    @DisplayName("Should throw exception when orderId is null on updateOrderToReady")
    void shouldThrowWhenOrderIdIsNullOnUpdateToReady() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.updateOrderToReady(null, 3L)
        );
        assertEquals("Order id cannot be null", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when employeeId is null on updateOrderToReady")
    void shouldThrowWhenEmployeeIdIsNullOnUpdateToReady() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.updateOrderToReady(100L, null)
        );
        assertEquals("Employee id cannot be null", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when employee is not assigned to any restaurant on updateOrderToReady")
    void shouldThrowWhenEmployeeNotAssignedToRestaurantOnUpdateToReady() {
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.empty());

        EmployeeNotAssignedToRestaurantException exception = assertThrows(
                EmployeeNotAssignedToRestaurantException.class,
                () -> orderUseCase.updateOrderToReady(100L, 3L)
        );
        assertEquals("The employee is not assigned to any restaurant", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order is not found on updateOrderToReady")
    void shouldThrowWhenOrderNotFoundOnUpdateToReady() {
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(100L)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderUseCase.updateOrderToReady(100L, 3L)
        );
        assertEquals("Order not found with id: 100", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order does not belong to employee's restaurant on updateOrderToReady")
    void shouldThrowWhenOrderDoesNotBelongToRestaurantOnUpdateToReady() {
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        Order orderFromOtherRestaurant = new Order(100L, 10L, null, OrderStatus.EN_PREPARACION, 3L, 2L, List.of());

        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(100L)).thenReturn(Optional.of(orderFromOtherRestaurant));

        OrderDoesNotBelongToRestaurantException exception = assertThrows(
                OrderDoesNotBelongToRestaurantException.class,
                () -> orderUseCase.updateOrderToReady(100L, 3L)
        );
        assertEquals("The order does not belong to the restaurant where the employee works", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order is not in EN_PREPARACION status on updateOrderToReady")
    void shouldThrowWhenStatusIsNotInPreparationOnUpdateToReady() {
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        Order orderInPendingStatus = new Order(100L, 10L, null, OrderStatus.PENDIENTE, null, 1L, List.of());

        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(100L)).thenReturn(Optional.of(orderInPendingStatus));

        InvalidOrderStatusException exception = assertThrows(
                InvalidOrderStatusException.class,
                () -> orderUseCase.updateOrderToReady(100L, 3L)
        );
        assertEquals("Only orders in EN_PREPARACION status can be marked as ready", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

}