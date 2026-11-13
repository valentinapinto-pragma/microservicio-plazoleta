package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.DishDoesNotBelongToRestaurantException;
import com.pragma.plazoleta.domain.exception.DishInactiveException;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.EmployeeNotAssignedToRestaurantException;
import com.pragma.plazoleta.domain.exception.InvalidOrderDataException;
import com.pragma.plazoleta.domain.exception.InvalidOrderStatusException;
import com.pragma.plazoleta.domain.exception.InvalidSecurityPinException;
import com.pragma.plazoleta.domain.exception.OrderCannotBeCancelledException;
import com.pragma.plazoleta.domain.exception.OrderDoesNotBelongToClientException;
import com.pragma.plazoleta.domain.exception.OrderDoesNotBelongToRestaurantException;
import com.pragma.plazoleta.domain.exception.OrderNotFoundException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.MessageNotificationPort;
import com.pragma.plazoleta.domain.repositoryPort.OrderPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantEmployeePersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserInformationPort;
import com.pragma.plazoleta.domain.servicePort.OrderServicePort;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderUseCase implements OrderServicePort {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static final int MIN_PAGE_NUMBER = 0;
    public static final int MIN_SIZE_REQUIRED = 0;
    private static final String EMPLOYEE_NOT_ASSIGNED_MESSAGE = "The employee is not assigned to any restaurant";
    private static final String ORDER_NOT_FOUND_MESSAGE = "Order not found with id: ";
    private static final String ORDER_CANNOT_BE_CANCELLED_MESSAGE = "Lo sentimos, tu pedido ya está en preparación y no puede cancelarse";

    private static final int PIN_LENGTH = 6;
    private static final int PIN_UPPER_BOUND = 1_000_000; // 10^PIN_LENGTH
    private static final String PIN_FORMAT = "%0" + PIN_LENGTH + "d";
    private static final String ORDER_READY_MESSAGE_TEMPLATE = "¡Hola! Tu pedido #%d está LISTO para ser reclamado. Tu PIN de seguridad es: %s";

    private final OrderPersistentPort orderPersistentPort;
    private final DishPersistentPort dishPersistentPort;
    private final RestaurantPersistentPort restaurantPersistentPort;
    private final RestaurantEmployeePersistentPort restaurantEmployeePersistentPort;
    private final UserInformationPort userInformationPort;
    private final MessageNotificationPort messageNotificationPort;

    public OrderUseCase(OrderPersistentPort orderPersistentPort,
                        DishPersistentPort dishPersistentPort,
                        RestaurantPersistentPort restaurantPersistentPort,
                        RestaurantEmployeePersistentPort restaurantEmployeePersistentPort,
                        UserInformationPort userInformationPort,
                        MessageNotificationPort messageNotificationPort) {
        this.orderPersistentPort = orderPersistentPort;
        this.dishPersistentPort = dishPersistentPort;
        this.restaurantPersistentPort = restaurantPersistentPort;
        this.restaurantEmployeePersistentPort = restaurantEmployeePersistentPort;
        this.userInformationPort = userInformationPort;
        this.messageNotificationPort = messageNotificationPort;
    }

    @Override
    public Order createOrder(Order order) {
        Long authenticatedClientId = userInformationPort.getAuthenticatedUserId();
        order.setClientId(authenticatedClientId);

        validateOrderHasDishes(order);
        validateClientId(order.getClientId());
        validateNoDuplicateDishes(order.getOrderDishes());
        validateRestaurantExists(order.getRestaurantId());
        validateClientHasNoActiveOrder(order.getClientId());
        validateOrderDishes(order);

        order.setStatus(OrderStatus.PENDIENTE);
        order.setDate(LocalDateTime.now(ZoneId.systemDefault()));

        return orderPersistentPort.save(order);
    }

    @Override
    public CustomPage<Order> getOrdersByStatus(OrderStatus status, int page, int size) {
        validateStatusNotNull(status);
        validatePagination(page, size);

        Long employeeId = userInformationPort.getAuthenticatedUserId();
        validateEmployeeIdNotNull(employeeId);

        RestaurantEmployee employee = restaurantEmployeePersistentPort.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotAssignedToRestaurantException(EMPLOYEE_NOT_ASSIGNED_MESSAGE));

        return orderPersistentPort.getOrdersByRestaurantAndStatus(
                employee.getRestaurantId(),
                status,
                page,
                size
        );
    }

    @Override
    public Order assignOrder(Long orderId) {
        validateOrderIdNotNull(orderId);

        Long employeeId = userInformationPort.getAuthenticatedUserId();
        validateEmployeeIdNotNull(employeeId);

        RestaurantEmployee employee = restaurantEmployeePersistentPort.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotAssignedToRestaurantException(EMPLOYEE_NOT_ASSIGNED_MESSAGE));

        Order order = getOrderOrThrow(orderId);

        validateOrderBelongsToRestaurant(order, employee.getRestaurantId());
        validateOrderIsInPendingStatus(order);

        order.setStatus(OrderStatus.EN_PREPARACION);
        order.setChefId(employeeId);

        return orderPersistentPort.save(order);
    }

    @Override
    public Order updateOrderToReady(Long orderId) {
        validateOrderIdNotNull(orderId);

        Long employeeId = userInformationPort.getAuthenticatedUserId();
        validateEmployeeIdNotNull(employeeId);

        RestaurantEmployee employee = restaurantEmployeePersistentPort.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotAssignedToRestaurantException(EMPLOYEE_NOT_ASSIGNED_MESSAGE));

        Order order = getOrderOrThrow(orderId);

        validateOrderBelongsToRestaurant(order, employee.getRestaurantId());
        validateOrderIsInPreparationStatus(order);

        String securityPin = generateSecurityPin();
        order.setStatus(OrderStatus.LISTO);
        order.setSecurityPin(securityPin);

        Order savedOrder = orderPersistentPort.save(order);

        notifyClientOrderReady(savedOrder, securityPin);

        return savedOrder;
    }

    @Override
    public Order deliverOrder(Long orderId, String securityPin) {
        validateOrderIdNotNull(orderId);
        validateSecurityPinNotNullOrBlank(securityPin);

        Long employeeId = userInformationPort.getAuthenticatedUserId();
        validateEmployeeIdNotNull(employeeId);

        RestaurantEmployee employee = restaurantEmployeePersistentPort.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotAssignedToRestaurantException(EMPLOYEE_NOT_ASSIGNED_MESSAGE));

        Order order = getOrderOrThrow(orderId);

        validateOrderBelongsToRestaurant(order, employee.getRestaurantId());
        validateOrderIsInReadyStatus(order);
        validateSecurityPinMatches(order, securityPin);

        order.setStatus(OrderStatus.ENTREGADO);

        return orderPersistentPort.save(order);
    }

    @Override
    public Order cancelOrder(Long orderId) {
        validateOrderIdNotNull(orderId);

        Long clientId = userInformationPort.getAuthenticatedUserId();
        validateClientId(clientId);

        Order order = getOrderOrThrow(orderId);

        validateOrderBelongsToClient(order, clientId);
        validateOrderIsInPendingStatusForCancellation(order);

        order.setStatus(OrderStatus.CANCELADO);

        return orderPersistentPort.save(order);
    }

    private Order getOrderOrThrow(Long orderId) {
        return orderPersistentPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderId));
    }

    private void validateOrderBelongsToClient(Order order, Long clientId) {
        if (!order.getClientId().equals(clientId)) {
            throw new OrderDoesNotBelongToClientException("The order does not belong to the authenticated client");
        }
    }

    private void validateOrderIsInPendingStatusForCancellation(Order order) {
        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new OrderCannotBeCancelledException(ORDER_CANNOT_BE_CANCELLED_MESSAGE);
        }
    }

    private void notifyClientOrderReady(Order order, String securityPin) {
        String clientPhone = userInformationPort.getClientPhoneNumber(order.getClientId());
        String message = String.format(ORDER_READY_MESSAGE_TEMPLATE, order.getId(), securityPin);
        messageNotificationPort.sendMessage(clientPhone, message);
    }

    private String generateSecurityPin() {
        int pinNumber = SECURE_RANDOM.nextInt(PIN_UPPER_BOUND);
        return String.format(PIN_FORMAT, pinNumber);
    }

    private void validateSecurityPinNotNullOrBlank(String securityPin) {
        if (securityPin == null || securityPin.isBlank()) {
            throw new InvalidSecurityPinException("Security PIN cannot be null or empty");
        }
    }

    private void validateOrderIsInReadyStatus(Order order) {
        if (order.getStatus() != OrderStatus.LISTO) {
            throw new InvalidOrderStatusException("Only orders in LISTO status can be delivered");
        }
    }

    private void validateSecurityPinMatches(Order order, String securityPin) {
        if (!securityPin.equals(order.getSecurityPin())) {
            throw new InvalidSecurityPinException("Invalid security PIN for order id: " + order.getId());
        }
    }

    private void validateOrderIsInPreparationStatus(Order order) {
        if (order.getStatus() != OrderStatus.EN_PREPARACION) {
            throw new InvalidOrderStatusException("Only orders in EN_PREPARACION status can be marked as ready");
        }
    }

    private void validateOrderBelongsToRestaurant(Order order, Long restaurantId) {
        if (!order.getRestaurantId().equals(restaurantId)) {
            throw new OrderDoesNotBelongToRestaurantException("The order does not belong to the restaurant where the employee works");
        }
    }

    private void validateOrderIsInPendingStatus(Order order) {
        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new InvalidOrderStatusException("Only orders in PENDIENTE status can be assigned to a chef");
        }
    }

    private void validateStatusNotNull(OrderStatus status) {
        if (status == null) {
            throw new InvalidOrderDataException("Order status cannot be null");
        }
    }

    private void validatePagination(int page, int size) {
        if (page < MIN_PAGE_NUMBER) {
            throw new InvalidOrderDataException("The page must be greater than or equal to 0");
        }
        if (size <= MIN_SIZE_REQUIRED) {
            throw new InvalidOrderDataException("The page size must be greater than 0");
        }
    }

    private void validateOrderIdNotNull(Long orderId) {
        if (orderId == null) {
            throw new InvalidOrderDataException("Order id cannot be null");
        }
    }

    private void validateEmployeeIdNotNull(Long employeeId) {
        if (employeeId == null) {
            throw new InvalidOrderDataException("Employee id cannot be null");
        }
    }

    private void validateOrderHasDishes(Order order) {
        if (order.getOrderDishes() == null || order.getOrderDishes().isEmpty()) {
            throw new InvalidOrderDataException("The order must contain at least one dish");
        }
    }

    private void validateClientId(Long clientId) {
        if (clientId == null) {
            throw new InvalidOrderDataException("Client id cannot be null");
        }
    }

    private void validateNoDuplicateDishes(List<OrderDish> orderDishes) {
        Set<Long> seenDishIds = new HashSet<>();
        for (OrderDish orderDish : orderDishes) {
            boolean wasAdded = seenDishIds.add(orderDish.getDishId());
            if (!wasAdded) {
                throw new InvalidOrderDataException("The order cannot contain duplicate dishes; please specify the total quantity");
            }
        }
    }

    private void validateRestaurantExists(Long restaurantId) {
        if (restaurantId == null || !restaurantPersistentPort.existsById(restaurantId)) {
            throw new RestaurantNotFoundException("Restaurant not found with id: " + restaurantId);
        }
    }

    private void validateClientHasNoActiveOrder(Long clientId) {
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.PENDIENTE,
                OrderStatus.EN_PREPARACION,
                OrderStatus.LISTO
        );
        if (orderPersistentPort.existsByClientIdAndStatusIn(clientId, activeStatuses)) {
            throw new ClientHasActiveOrderException("The client already has an active order in progress");
        }
    }

    private void validateOrderDishes(Order order) {
        for (OrderDish orderDish : order.getOrderDishes()) {
            validateSingleOrderDish(orderDish, order.getRestaurantId());
        }
    }

    private void validateSingleOrderDish(OrderDish orderDish, Long restaurantId) {
        validateQuantity(orderDish.getQuantity());
        Dish dish = getDishOrThrow(orderDish.getDishId());
        validateDishBelongsToRestaurant(dish, restaurantId);
        validateDishIsActive(dish);
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidOrderDataException("The dish quantity must be greater than zero");
        }
    }

    private Dish getDishOrThrow(Long dishId) {
        return dishPersistentPort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException("Dish not found with id: " + dishId));
    }

    private void validateDishBelongsToRestaurant(Dish dish, Long restaurantId) {
        if (!dish.getIdRestaurant().equals(restaurantId)) {
            throw new DishDoesNotBelongToRestaurantException(
                    "The dish with id " + dish.getId() + " does not belong to restaurant with id " + restaurantId
            );
        }
    }

    private void validateDishIsActive(Dish dish) {
        if (!Boolean.TRUE.equals(dish.getActive())) {
            throw new DishInactiveException("The dish '" + dish.getName() + "' is currently inactive");
        }
    }
}