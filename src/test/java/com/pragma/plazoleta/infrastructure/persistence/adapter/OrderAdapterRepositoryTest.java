package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.infrastructure.persistence.entity.CategoryEntity;
import com.pragma.plazoleta.infrastructure.persistence.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.persistence.entity.OrderDishEntity;
import com.pragma.plazoleta.infrastructure.persistence.entity.OrderEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.DishEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.mapper.OrderEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.repository.DishRepository;
import com.pragma.plazoleta.infrastructure.persistence.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderAdapterRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntityMapper orderEntityMapper;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DishEntityMapper dishEntityMapper;

    @InjectMocks
    private OrderAdapterRepository orderAdapterRepository;

    private Order order;
    private OrderEntity orderEntity;
    private Dish sampleDish;
    private DishEntity sampleDishEntity;

    @BeforeEach
    void setUp() {
        order = new Order(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, new ArrayList<>());
        orderEntity = new OrderEntity(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, new ArrayList<>());

        Category category = new Category(1L, "Comida Rápida", "Platos rápidos");
        sampleDish = new Dish(100L, "Hamburguesa", 25000, "Deliciosa", "url", category, 1L, true);

        CategoryEntity categoryEntity = new CategoryEntity(1L, "Comida Rápida", "Platos rápidos");
        sampleDishEntity = new DishEntity(100L, "Hamburguesa", 25000, "Deliciosa", "url", categoryEntity, 1L, true);
    }

    @Test
    @DisplayName("Should successfully save order through repository and mapper")
    void shouldSaveOrderSuccessfully() {
        // Arrange
        when(orderEntityMapper.toEntity(order)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);

        // Act
        Order result = orderAdapterRepository.save(order);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        verify(orderEntityMapper, times(1)).toEntity(order);
        verify(orderRepository, times(1)).save(orderEntity);
        verify(orderEntityMapper, times(1)).toDomain(orderEntity);
    }

    @Test
    @DisplayName("Should successfully save and load dish details when order has dishes")
    void shouldSaveAndLoadDishDetailsWhenOrderHasDishes() {
        // Arrange
        OrderDish orderDish = new OrderDish(1L, 1L, 100L, 2);
        Order orderWithDishes = new Order(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, List.of(orderDish));

        when(orderEntityMapper.toEntity(orderWithDishes)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(orderWithDishes);
        when(dishRepository.findAllById(Set.of(100L))).thenReturn(List.of(sampleDishEntity));
        when(dishEntityMapper.toDomain(sampleDishEntity)).thenReturn(sampleDish);

        // Act
        Order result = orderAdapterRepository.save(orderWithDishes);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrderDishes().size());
        assertEquals(sampleDish, result.getOrderDishes().get(0).getDish());
        verify(dishRepository, times(1)).findAllById(Set.of(100L));
        verify(dishEntityMapper, times(1)).toDomain(sampleDishEntity);
    }

    @Test
    @DisplayName("Should return true when active order exists for client")
    void shouldReturnTrueWhenActiveOrderExists() {
        // Arrange
        List<OrderStatus> statuses = List.of(OrderStatus.PENDIENTE, OrderStatus.EN_PREPARACION, OrderStatus.LISTO);
        when(orderRepository.existsByIdClientAndStatusIn(10L, statuses)).thenReturn(true);

        // Act
        boolean exists = orderAdapterRepository.existsByClientIdAndStatusIn(10L, statuses);

        // Assert
        assertTrue(exists);
        verify(orderRepository, times(1)).existsByIdClientAndStatusIn(10L, statuses);
    }

    @Test
    @DisplayName("Should return false when active order does not exist for client")
    void shouldReturnFalseWhenActiveOrderDoesNotExist() {
        // Arrange
        List<OrderStatus> statuses = List.of(OrderStatus.PENDIENTE, OrderStatus.EN_PREPARACION, OrderStatus.LISTO);
        when(orderRepository.existsByIdClientAndStatusIn(10L, statuses)).thenReturn(false);

        // Act
        boolean exists = orderAdapterRepository.existsByClientIdAndStatusIn(10L, statuses);

        // Assert
        assertFalse(exists);
        verify(orderRepository, times(1)).existsByIdClientAndStatusIn(10L, statuses);
    }

    @Test
    @DisplayName("Should successfully get orders by restaurant and status with pagination")
    void shouldGetOrdersByRestaurantAndStatusSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderEntity> orderPage = new PageImpl<>(List.of(orderEntity), pageable, 1);
        when(orderRepository.findByIdRestaurantAndStatus(1L, OrderStatus.PENDIENTE, pageable)).thenReturn(orderPage);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);

        // Act
        CustomPage<Order> result = orderAdapterRepository.getOrdersByRestaurantAndStatus(1L, OrderStatus.PENDIENTE, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(order.getId(), result.getContent().get(0).getId());
        verify(orderRepository, times(1)).findByIdRestaurantAndStatus(1L, OrderStatus.PENDIENTE, pageable);
        verify(orderEntityMapper, times(1)).toDomain(orderEntity);
    }

    @Test
    @DisplayName("Should return empty custom page when no orders match restaurant and status")
    void shouldReturnEmptyListWhenNoOrdersMatch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(orderRepository.findByIdRestaurantAndStatus(1L, OrderStatus.PENDIENTE, pageable)).thenReturn(emptyPage);

        // Act
        CustomPage<Order> result = orderAdapterRepository.getOrdersByRestaurantAndStatus(1L, OrderStatus.PENDIENTE, 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        verify(orderRepository, times(1)).findByIdRestaurantAndStatus(1L, OrderStatus.PENDIENTE, pageable);
        verify(orderEntityMapper, never()).toDomain(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should return order when found by id and load dish details")
    void shouldReturnOrderWhenFoundById() {
        // Arrange
        OrderDish orderDish = new OrderDish(1L, 1L, 100L, 2);
        Order orderWithDishes = new Order(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, List.of(orderDish));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(orderWithDishes);
        when(dishRepository.findAllById(Set.of(100L))).thenReturn(List.of(sampleDishEntity));
        when(dishEntityMapper.toDomain(sampleDishEntity)).thenReturn(sampleDish);

        // Act
        Optional<Order> result = orderAdapterRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(sampleDish, result.get().getOrderDishes().get(0).getDish());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return all orders with loaded dish details")
    void shouldReturnAllOrders() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(List.of(orderEntity));
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);

        // Act
        List<Order> result = orderAdapterRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should delete order by id")
    void shouldDeleteOrderById() {
        // Act
        orderAdapterRepository.delete(1L);

        // Assert
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return order as-is when order has null dishes list")
    void shouldReturnOrderAsIsWhenDishesListIsNull() {
        // Arrange
        Order orderWithNullDishes = new Order(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(orderWithNullDishes);

        // Act
        Optional<Order> result = orderAdapterRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertNull(result.get().getOrderDishes());
        verify(dishRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("Should return order as-is when all dish ids are null")
    void shouldReturnOrderAsIsWhenAllDishIdsAreNull() {
        // Arrange
        OrderDish orderDishWithNullId = new OrderDish(1L, 1L, null, 2);
        Order orderWithNullDishId = new Order(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, List.of(orderDishWithNullId));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(orderWithNullDishId);

        // Act
        Optional<Order> result = orderAdapterRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getOrderDishes().size());
        assertNull(result.get().getOrderDishes().get(0).getDish());
        verify(dishRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("Should handle duplicate dishes in database without throwing error")
    void shouldHandleDuplicateDishesWithoutError() {
        // Arrange
        OrderDish orderDish = new OrderDish(1L, 1L, 100L, 2);
        Order orderWithDishes = new Order(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, List.of(orderDish));

        // Simulamos que por alguna razón la BD devuelve 2 entidades con el mismo ID 100L
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(orderWithDishes);
        when(dishRepository.findAllById(Set.of(100L))).thenReturn(List.of(sampleDishEntity, sampleDishEntity));
        when(dishEntityMapper.toDomain(sampleDishEntity)).thenReturn(sampleDish);

        // Act
        Optional<Order> result = orderAdapterRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(sampleDish, result.get().getOrderDishes().get(0).getDish());
    }
}