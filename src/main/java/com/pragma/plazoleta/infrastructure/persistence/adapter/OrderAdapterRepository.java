package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.repositoryPort.OrderPersistentPort;
import com.pragma.plazoleta.infrastructure.persistence.entity.OrderDishEntity;
import com.pragma.plazoleta.infrastructure.persistence.entity.OrderEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.DishEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.mapper.OrderEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.repository.DishRepository;
import com.pragma.plazoleta.infrastructure.persistence.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderAdapterRepository implements OrderPersistentPort {

    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final DishRepository dishRepository;
    private final DishEntityMapper dishEntityMapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = orderEntityMapper.toEntity(order);
        OrderEntity savedEntity = orderRepository.save(entity);
        return hydrateOrder(savedEntity);
    }

    @Override
    public boolean existsByClientIdAndStatusIn(Long clientId, List<OrderStatus> statuses) {
        return orderRepository.existsByIdClientAndStatusIn(clientId, statuses);
    }

    @Override
    public CustomPage<Order> getOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> pageResult = orderRepository.findByIdRestaurantAndStatus(restaurantId, status, pageable);
        List<Order> orders = hydrateOrders(pageResult.getContent());
        return new CustomPage<>(
                orders,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id).map(this::hydrateOrder);
    }

    @Override
    public List<Order> findAll() {
        return hydrateOrders(orderRepository.findAll());
    }

    @Override
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    private Order hydrateOrder(OrderEntity orderEntity) {
        return hydrateOrders(List.of(orderEntity)).stream().findFirst().orElse(null);
    }

    private List<Order> hydrateOrders(List<OrderEntity> orderEntities) {
        if (orderEntities == null || orderEntities.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> dishIds = extractUniqueDishIds(orderEntities);
        Map<Long, Dish> dishLookup = createDishLookup(dishIds);

        return orderEntities.stream()
                .filter(Objects::nonNull)
                .map(entity -> toFullyHydratedOrder(entity, dishLookup))
                .collect(Collectors.toList());
    }

    private Set<Long> extractUniqueDishIds(List<OrderEntity> orderEntities) {
        return orderEntities.stream()
                .filter(order -> order != null && order.getOrderDishes() != null)
                .flatMap(order -> order.getOrderDishes().stream())
                .filter(Objects::nonNull)
                .map(OrderDishEntity::getIdDish)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Map<Long, Dish> createDishLookup(Set<Long> dishIds) {
        if (dishIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return dishRepository.findAllById(dishIds).stream()
                .map(dishEntityMapper::toDomain)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Dish::getId, Function.identity()));
    }

    private Order toFullyHydratedOrder(OrderEntity entity, Map<Long, Dish> dishLookup) {
        List<OrderDish> hydratedDishes = Optional.ofNullable(entity.getOrderDishes())
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(dishEntity -> toHydratedOrderDish(dishEntity, dishLookup))
                .collect(Collectors.toList());

        return new Order(
                entity.getId(),
                entity.getIdClient(),
                entity.getDate(),
                entity.getStatus(),
                entity.getIdChef(),
                entity.getIdRestaurant(),
                hydratedDishes,
                entity.getSecurityPin()
        );
    }

    private OrderDish toHydratedOrderDish(OrderDishEntity dishEntity, Map<Long, Dish> dishLookup) {
        Long dishId = dishEntity.getIdDish();
        Dish dish = dishId != null ? dishLookup.get(dishId) : null;
        Long orderId = dishEntity.getOrder() != null ? dishEntity.getOrder().getId() : null;

        return new OrderDish(
                dishEntity.getId(),
                orderId,
                dishId,
                dishEntity.getQuantity(),
                dish
        );
    }
}