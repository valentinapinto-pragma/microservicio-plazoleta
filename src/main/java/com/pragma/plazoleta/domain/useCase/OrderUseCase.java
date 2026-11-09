package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.DishDoesNotBelongToRestaurantException;
import com.pragma.plazoleta.domain.exception.DishInactiveException;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.EmployeeNotAssignedToRestaurantException;
import com.pragma.plazoleta.domain.exception.InvalidOrderDataException;
import com.pragma.plazoleta.domain.exception.InvalidOrderStatusException;
import com.pragma.plazoleta.domain.exception.OrderDoesNotBelongToRestaurantException;
import com.pragma.plazoleta.domain.exception.OrderNotFoundException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
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
import java.util.List;

public class OrderUseCase implements OrderServicePort {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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
        validateOrderHasDishes(order);
        validateClientId(order.getClientId());
        validateNoDuplicateDishes(order.getOrderDishes());
        validateRestaurantExists(order.getRestaurantId());
        validateClientHasNoActiveOrder(order.getClientId());
        validateOrderDishes(order);

        order.setStatus(OrderStatus.PENDIENTE);
        order.setDate(LocalDateTime.now(ZoneId.systemDefault()));
        order.setChefId(null);

        return orderPersistentPort.save(order);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status, int page, int size, Long employeeId) {
        validateStatusNotNull(status);
        validatePagination(page, size);
        validateEmployeeIdNotNull(employeeId);

        RestaurantEmployee employee = restaurantEmployeePersistentPort.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotAssignedToRestaurantException("The employee is not assigned to any restaurant"));

        List<Order> orders = orderPersistentPort.getOrdersByRestaurantAndStatus(
                employee.getRestaurantId(),
                status,
                page,
                size
        );

        enrichOrdersWithDishDetails(orders);
        return orders;
    }

    @Override
    public Order assignOrder(Long orderId, OrderStatus status, Long employeeId) {
        validateOrderIdNotNull(orderId);
        validateStatusNotNull(status);
        validateEmployeeIdNotNull(employeeId);
        validateStatusIsInPreparation(status);

        RestaurantEmployee employee = restaurantEmployeePersistentPort.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotAssignedToRestaurantException("The employee is not assigned to any restaurant"));

        Order order = orderPersistentPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        validateOrderBelongsToRestaurant(order, employee.getRestaurantId());
        validateOrderIsInPendingStatus(order);

        order.setStatus(OrderStatus.EN_PREPARACION);
        order.setChefId(employeeId);

        Order savedOrder = orderPersistentPort.save(order);
        enrichOrdersWithDishDetails(List.of(savedOrder));
        return savedOrder;
    }

    @Override
    public Order updateOrderToReady(Long orderId, Long employeeId) {
        validateOrderIdNotNull(orderId);
        validateEmployeeIdNotNull(employeeId);

        RestaurantEmployee employee = restaurantEmployeePersistentPort.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotAssignedToRestaurantException("The employee is not assigned to any restaurant"));

        Order order = orderPersistentPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        validateOrderBelongsToRestaurant(order, employee.getRestaurantId());
        validateOrderIsInPreparationStatus(order);

        String securityPin = generateSecurityPin();
        order.setStatus(OrderStatus.LISTO);
        order.setSecurityPin(securityPin);

        Order savedOrder = orderPersistentPort.save(order);

        notifyClientOrderReady(savedOrder, securityPin);

        enrichOrdersWithDishDetails(List.of(savedOrder));
        return savedOrder;
    }

    private void notifyClientOrderReady(Order order, String securityPin) {
        if (messageNotificationPort != null && userInformationPort != null) {
            String clientPhone = userInformationPort.getClientPhoneNumber(order.getClientId());
            String message = String.format("¡Hola! Tu pedido #%d está LISTO para ser reclamado. Tu PIN de seguridad es: %s", order.getId(), securityPin);
            messageNotificationPort.sendMessage(clientPhone, message);
        }
    }

    private String generateSecurityPin() {
        int pinNumber = SECURE_RANDOM.nextInt(1_000_000);
        return String.format("%06d", pinNumber);
    }

    private void validateOrderIsInPreparationStatus(Order order) {
        if (order.getStatus() != OrderStatus.EN_PREPARACION) {
            throw new InvalidOrderStatusException("Only orders in EN_PREPARACION status can be marked as ready");
        }
    }

    private void validateOrderIdNotNull(Long orderId) {
        if (orderId == null) {
            throw new InvalidOrderDataException("Order id cannot be null");
        }
    }

    private void validateStatusIsInPreparation(OrderStatus status) {
        if (status != OrderStatus.EN_PREPARACION) {
            throw new InvalidOrderStatusException("The requested status transition is invalid; only EN_PREPARACION is allowed for order assignment");
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
        if (page < 0) {
            throw new InvalidOrderDataException("The page must be greater than or equal to 0");
        }
        if (size <= 0) {
            throw new InvalidOrderDataException("The page size must be greater than 0");
        }
    }

    private void validateEmployeeIdNotNull(Long employeeId) {
        if (employeeId == null) {
            throw new InvalidOrderDataException("Employee id cannot be null");
        }
    }

    private void enrichOrdersWithDishDetails(List<Order> orders) {
        for (Order order : orders) {
            if (order.getOrderDishes() != null) {
                for (OrderDish orderDish : order.getOrderDishes()) {
                    dishPersistentPort.findById(orderDish.getDishId())
                            .ifPresent(orderDish::setDish);
                }
            }
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
        long uniqueDishes = orderDishes.stream()
                .map(OrderDish::getDishId)
                .distinct()
                .count();
        if (uniqueDishes < orderDishes.size()) {
            throw new InvalidOrderDataException("The order cannot contain duplicate dishes; please specify the total quantity");
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