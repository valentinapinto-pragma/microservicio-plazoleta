package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.repositoryPort.OrderPersistentPort;
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
        return loadDishDetails(orderEntityMapper.toDomain(savedEntity));
    }

    @Override
    public boolean existsByClientIdAndStatusIn(Long clientId, List<OrderStatus> statuses) {
        return orderRepository.existsByIdClientAndStatusIn(clientId, statuses);
    }

    @Override
    public CustomPage<Order> getOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> pageResult = orderRepository.findByIdRestaurantAndStatus(restaurantId, status, pageable);
        List<Order> orders = pageResult.getContent()
                .stream()
                .map(orderEntityMapper::toDomain)
                .map(this::loadDishDetails)
                .collect(Collectors.toList());
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
        return orderRepository.findById(id)
                .map(orderEntityMapper::toDomain)
                .map(this::loadDishDetails);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderEntityMapper::toDomain)
                .map(this::loadDishDetails)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    private Order loadDishDetails(Order order) {
        if (order == null || order.getOrderDishes() == null || order.getOrderDishes().isEmpty()) {
            return order;
        }

        Set<Long> dishIds = order.getOrderDishes().stream()
                .map(OrderDish::getDishId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (dishIds.isEmpty()) {
            return order;
        }

        Map<Long, Dish> dishesMap = dishRepository.findAllById(dishIds).stream()
                .map(dishEntityMapper::toDomain)
                .collect(Collectors.toMap(
                        Dish::getId,
                        dish -> dish,
                        (existing, replacement) -> existing
                ));

        List<OrderDish> dishesWithDetails = order.getOrderDishes().stream()
                .map(od -> new OrderDish(
                        od.getId(),
                        od.getOrderId(),
                        od.getDishId(),
                        od.getQuantity(),
                        dishesMap.get(od.getDishId())
                ))
                .collect(Collectors.toList());

        return new Order(
                order.getId(),
                order.getClientId(),
                order.getDate(),
                order.getStatus(),
                order.getChefId(),
                order.getRestaurantId(),
                dishesWithDetails,
                order.getSecurityPin()
        );
    }
}