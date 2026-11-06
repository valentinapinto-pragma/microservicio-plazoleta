package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.DishDoesNotBelongToRestaurantException;
import com.pragma.plazoleta.domain.exception.DishInactiveException;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.EmployeeNotAssignedToRestaurantException;
import com.pragma.plazoleta.domain.exception.InvalidOrderDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.OrderPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantEmployeePersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.servicePort.OrderServicePort;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class OrderUseCase implements OrderServicePort {

    private final OrderPersistentPort orderPersistentPort;
    private final DishPersistentPort dishPersistentPort;
    private final RestaurantPersistentPort restaurantPersistentPort;
    private final RestaurantEmployeePersistentPort restaurantEmployeePersistentPort;

    public OrderUseCase(OrderPersistentPort orderPersistentPort,
                        DishPersistentPort dishPersistentPort,
                        RestaurantPersistentPort restaurantPersistentPort,
                        RestaurantEmployeePersistentPort restaurantEmployeePersistentPort) {
        this.orderPersistentPort = orderPersistentPort;
        this.dishPersistentPort = dishPersistentPort;
        this.restaurantPersistentPort = restaurantPersistentPort;
        this.restaurantEmployeePersistentPort = restaurantEmployeePersistentPort;
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