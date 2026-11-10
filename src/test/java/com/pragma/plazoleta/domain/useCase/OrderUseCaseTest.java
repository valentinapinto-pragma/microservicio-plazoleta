package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.*;
import com.pragma.plazoleta.domain.model.*;
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

        lenient().when(userInformationPort.getAuthenticatedUserId()).thenReturn(10L);
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
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(null);

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
        verifyNoInteractions(restaurantPersistentPort, orderPersistentPort);
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
    @DisplayName("Should throw exception when client already has an active order")
    void shouldThrowExceptionWhenClientHasActiveOrder() {
        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(true);

        ClientHasActiveOrderException exception = assertThrows(
                ClientHasActiveOrderException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The client already has an active order in progress", exception.getMessage());
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
    @DisplayName("Should throw exception when dish does not belong to the restaurant")
    void shouldThrowExceptionWhenDishDoesNotBelongToRestaurant() {
        validDish1.setIdRestaurant(99L); // Restaurante ajeno

        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(validDish1));

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
        validDish1.setActive(false); // Plato deshabilitado

        when(restaurantPersistentPort.existsById(1L)).thenReturn(true);
        when(orderPersistentPort.existsByClientIdAndStatusIn(eq(10L), anyList())).thenReturn(false);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(validDish1));

        DishInactiveException exception = assertThrows(
                DishInactiveException.class,
                () -> orderUseCase.createOrder(validOrder)
        );
        assertEquals("The dish 'Hamburguesa' is currently inactive", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when dish quantity is zero or negative")
    void shouldThrowExceptionWhenDishQuantityIsZeroOrNegative() {
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

    // TESTS: HU-12 - getOrdersByStatus

    @Test
    @DisplayName("Should successfully get orders by status when employee belongs to restaurant")
    void shouldGetOrdersByStatusSuccessfully() {
        Long employeeId = 50L;
        Long restaurantId = 1L;
        RestaurantEmployee employee = new RestaurantEmployee(1L, restaurantId, employeeId);
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(employeeId);
        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        Order existingOrder = new Order(1000L, 10L, null, OrderStatus.PENDIENTE, null, restaurantId, validOrder.getOrderDishes());
        CustomPage<Order> customPage = new CustomPage<>(List.of(existingOrder), 0, 10, 1L, 1);
        when(orderPersistentPort.getOrdersByRestaurantAndStatus(restaurantId, OrderStatus.PENDIENTE, 0, 10))
                .thenReturn(customPage);
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(validDish1));
        when(dishPersistentPort.findById(101L)).thenReturn(Optional.of(validDish2));

        CustomPage<Order> result = orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10);

        assertNotNull(result);
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        Order returnedOrder = result.getContent().get(0);
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
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(employeeId);
        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        EmployeeNotAssignedToRestaurantException exception = assertThrows(
                EmployeeNotAssignedToRestaurantException.class,
                () -> orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10)
        );
        assertEquals("The employee is not assigned to any restaurant", exception.getMessage());
        verify(orderPersistentPort, never()).getOrdersByRestaurantAndStatus(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw exception when status is null")
    void shouldThrowExceptionWhenStatusIsNull() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.getOrdersByStatus(null, 0, 10)
        );
        assertEquals("Order status cannot be null", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when page is negative")
    void shouldThrowExceptionWhenPageIsNegative() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, -1, 10)
        );
        assertEquals("The page must be greater than or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when size is zero or negative")
    void shouldThrowExceptionWhenSizeIsZeroOrNegative() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 0)
        );
        assertEquals("The page size must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when employee id is null")
    void shouldThrowExceptionWhenEmployeeIdIsNull() {
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(null);

        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10)
        );
        assertEquals("Employee id cannot be null", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }

    @Test
    @DisplayName("Should return empty page when no orders match status and pagination")
    void shouldReturnEmptyListWhenNoOrdersFound() {
        Long employeeId = 50L;
        Long restaurantId = 1L;
        RestaurantEmployee employee = new RestaurantEmployee(1L, restaurantId, employeeId);
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(employeeId);
        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        CustomPage<Order> emptyPage = new CustomPage<>(Collections.emptyList(), 0, 10, 0L, 0);
        when(orderPersistentPort.getOrdersByRestaurantAndStatus(restaurantId, OrderStatus.PENDIENTE, 0, 10))
                .thenReturn(emptyPage);
        CustomPage<Order> result = orderUseCase.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10);
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        verify(orderPersistentPort, times(1)).getOrdersByRestaurantAndStatus(restaurantId, OrderStatus.PENDIENTE, 0, 10);
        verify(dishPersistentPort, never()).findById(any());
    }

    // TESTS: HU-13 - assignOrder

    @Test
    @DisplayName("Should successfully assign order to authenticated employee and change status to EN_PREPARACION")
    void shouldAssignOrderSuccessfully() {
        Long orderId = 100L;
        Long employeeId = 3L;
        Long restaurantId = 1L;

        RestaurantEmployee employee = new RestaurantEmployee(1L, restaurantId, employeeId);
        Order existingOrder = new Order(orderId, 10L, null, OrderStatus.PENDIENTE, null, restaurantId, validOrder.getOrderDishes());

        when(userInformationPort.getAuthenticatedUserId()).thenReturn(employeeId);
        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderPersistentPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(validDish1));
        when(dishPersistentPort.findById(101L)).thenReturn(Optional.of(validDish2));

        Order result = orderUseCase.assignOrder(orderId, OrderStatus.EN_PREPARACION);

        assertNotNull(result);
        assertEquals(OrderStatus.EN_PREPARACION, result.getStatus());
        assertEquals(employeeId, result.getChefId());
        verify(orderPersistentPort, times(1)).save(existingOrder);
        verify(dishPersistentPort, times(1)).findById(100L);
        verify(dishPersistentPort, times(1)).findById(101L);
    }

    @Test
    @DisplayName("Should throw exception when orderId is null on assign")
    void shouldThrowWhenOrderIdIsNullOnAssign() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.assignOrder(null, OrderStatus.EN_PREPARACION)
        );
        assertEquals("Order id cannot be null", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when status is null on assign")
    void shouldThrowWhenStatusIsNullOnAssign() {
        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.assignOrder(100L, null)
        );
        assertEquals("Order status cannot be null", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when employeeId is null on assign")
    void shouldThrowWhenEmployeeIdIsNullOnAssign() {
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(null);

        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.assignOrder(100L, OrderStatus.EN_PREPARACION)
        );
        assertEquals("Employee id cannot be null", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when requested status is not EN_PREPARACION on assign")
    void shouldThrowWhenStatusIsNotInPreparationOnAssign() {
        InvalidOrderStatusException exception = assertThrows(
                InvalidOrderStatusException.class,
                () -> orderUseCase.assignOrder(100L, OrderStatus.LISTO)
        );
        assertEquals("Status transition is invalid; only EN_PREPARACION is allowed for order assignment", exception.getMessage());
        verifyNoInteractions(restaurantEmployeePersistentPort, orderPersistentPort);
    }

    @Test
    @DisplayName("Should throw exception when employee is not assigned to any restaurant on assign")
    void shouldThrowWhenEmployeeNotAssignedToRestaurantOnAssign() {
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(3L);
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.empty());

        EmployeeNotAssignedToRestaurantException exception = assertThrows(
                EmployeeNotAssignedToRestaurantException.class,
                () -> orderUseCase.assignOrder(100L, OrderStatus.EN_PREPARACION)
        );
        assertEquals("The employee is not assigned to any restaurant", exception.getMessage());
        verify(orderPersistentPort, never()).findById(any());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order does not exist on assign")
    void shouldThrowWhenOrderNotFoundOnAssign() {
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(3L);
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(999L)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderUseCase.assignOrder(999L, OrderStatus.EN_PREPARACION)
        );
        assertEquals("Order not found with id: 999", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order belongs to a different restaurant on assign")
    void shouldThrowWhenOrderDoesNotBelongToEmployeeRestaurantOnAssign() {
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        Order orderFromOtherRestaurant = new Order(100L, 10L, null, OrderStatus.PENDIENTE, null, 2L, List.of());

        when(userInformationPort.getAuthenticatedUserId()).thenReturn(3L);
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(100L)).thenReturn(Optional.of(orderFromOtherRestaurant));

        OrderDoesNotBelongToRestaurantException exception = assertThrows(
                OrderDoesNotBelongToRestaurantException.class,
                () -> orderUseCase.assignOrder(100L, OrderStatus.EN_PREPARACION)
        );
        assertEquals("The order does not belong to the restaurant where the employee works", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order is not in PENDIENTE status on assign")
    void shouldThrowWhenOrderIsNotInPendingStatusOnAssign() {
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        Order orderAlreadyInPreparation = new Order(100L, 10L, null, OrderStatus.EN_PREPARACION, 2L, 1L, List.of());

        when(userInformationPort.getAuthenticatedUserId()).thenReturn(3L);
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(100L)).thenReturn(Optional.of(orderAlreadyInPreparation));

        InvalidOrderStatusException exception = assertThrows(
                InvalidOrderStatusException.class,
                () -> orderUseCase.assignOrder(100L, OrderStatus.EN_PREPARACION)
        );
        assertEquals("Only orders in PENDIENTE status can be assigned to a chef", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    // TESTS: HU-14 - updateOrderToReady

    @Test
    @DisplayName("Should successfully update order to ready, generate PIN, notify client, and enrich dishes")
    void shouldUpdateOrderToReadySuccessfully() {
        Long orderId = 100L;
        Long employeeId = 3L;
        Long clientId = 10L;
        Long restaurantId = 1L;

        RestaurantEmployee employee = new RestaurantEmployee(1L, restaurantId, employeeId);
        List<OrderDish> dishes = new ArrayList<>();
        dishes.add(new OrderDish(1L, null, 100L, 2));

        Order existingOrder = new Order(orderId, clientId, null, OrderStatus.EN_PREPARACION, employeeId, restaurantId, dishes);

        when(userInformationPort.getAuthenticatedUserId()).thenReturn(employeeId);
        when(restaurantEmployeePersistentPort.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderPersistentPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userInformationPort.getClientPhoneNumber(clientId)).thenReturn("+573001234567");
        when(dishPersistentPort.findById(100L)).thenReturn(Optional.of(validDish1));

        Order result = orderUseCase.updateOrderToReady(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.LISTO, result.getStatus());
        assertNotNull(result.getSecurityPin());
        assertTrue(result.getSecurityPin().matches("\\d{6}"));
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
                () -> orderUseCase.updateOrderToReady(null)
        );
        assertEquals("Order id cannot be null", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when employeeId is null on updateOrderToReady")
    void shouldThrowWhenEmployeeIdIsNullOnUpdateToReady() {
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(null);

        InvalidOrderDataException exception = assertThrows(
                InvalidOrderDataException.class,
                () -> orderUseCase.updateOrderToReady(100L)
        );
        assertEquals("Employee id cannot be null", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when employee is not assigned to any restaurant on updateOrderToReady")
    void shouldThrowWhenEmployeeNotAssignedToRestaurantOnUpdateToReady() {
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(3L);
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.empty());

        EmployeeNotAssignedToRestaurantException exception = assertThrows(
                EmployeeNotAssignedToRestaurantException.class,
                () -> orderUseCase.updateOrderToReady(100L)
        );
        assertEquals("The employee is not assigned to any restaurant", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order is not found on updateOrderToReady")
    void shouldThrowWhenOrderNotFoundOnUpdateToReady() {
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        when(userInformationPort.getAuthenticatedUserId()).thenReturn(3L);
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(100L)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderUseCase.updateOrderToReady(100L)
        );
        assertEquals("Order not found with id: 100", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order does not belong to employee's restaurant on updateOrderToReady")
    void shouldThrowWhenOrderDoesNotBelongToRestaurantOnUpdateToReady() {
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        Order orderFromOtherRestaurant = new Order(100L, 10L, null, OrderStatus.EN_PREPARACION, 3L, 2L, List.of());

        when(userInformationPort.getAuthenticatedUserId()).thenReturn(3L);
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(100L)).thenReturn(Optional.of(orderFromOtherRestaurant));

        OrderDoesNotBelongToRestaurantException exception = assertThrows(
                OrderDoesNotBelongToRestaurantException.class,
                () -> orderUseCase.updateOrderToReady(100L)
        );
        assertEquals("The order does not belong to the restaurant where the employee works", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order is not in EN_PREPARACION status on updateOrderToReady")
    void shouldThrowWhenStatusIsNotInPreparationOnUpdateToReady() {
        RestaurantEmployee employee = new RestaurantEmployee(1L, 1L, 3L);
        Order orderInPendingStatus = new Order(100L, 10L, null, OrderStatus.PENDIENTE, null, 1L, List.of());

        when(userInformationPort.getAuthenticatedUserId()).thenReturn(3L);
        when(restaurantEmployeePersistentPort.findByEmployeeId(3L)).thenReturn(Optional.of(employee));
        when(orderPersistentPort.findById(100L)).thenReturn(Optional.of(orderInPendingStatus));

        InvalidOrderStatusException exception = assertThrows(
                InvalidOrderStatusException.class,
                () -> orderUseCase.updateOrderToReady(100L)
        );
        assertEquals("Only orders in EN_PREPARACION status can be marked as ready", exception.getMessage());
        verify(orderPersistentPort, never()).save(any());
    }
}