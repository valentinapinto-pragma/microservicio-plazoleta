package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
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
import java.util.*;

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
        LocalDateTime now = LocalDateTime.now();
        order = new Order(1L, 10L, now, OrderStatus.PENDIENTE, null, 1L, new ArrayList<>());
        orderEntity = new OrderEntity(1L, 10L, now, OrderStatus.PENDIENTE, null, 1L, new ArrayList<>());

        Category category = new Category(1L, "Fast Food", "Fast Food Description");
        sampleDish = new Dish(100L, "Burger", 15000, "Delicious burger", "http://image.png", category, 1L, true);

        CategoryEntity categoryEntity = new CategoryEntity(1L, "Fast Food", "Fast Food Description");
        sampleDishEntity = new DishEntity(100L, "Burger", 15000, "Delicious burger", "http://image.png", categoryEntity, 1L, true);
    }

    @Test
    @DisplayName("Should successfully save order and return domain order with empty dishes")
    void shouldSaveOrderSuccessfully() {
        // Arrange
        when(orderEntityMapper.toEntity(order)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);

        // Act
        Order result = orderAdapterRepository.save(order);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getClientId(), result.getClientId());
        assertEquals(order.getStatus(), result.getStatus());
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    @DisplayName("Should successfully save and load dish details when order has dishes")
    void shouldSaveAndLoadDishDetailsWhenOrderHasDishes() {
        // Arrange
        OrderDishEntity orderDishEntity = new OrderDishEntity(1L, orderEntity, 100L, 2);
        OrderEntity entityWithDishes = new OrderEntity(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, List.of(orderDishEntity));

        when(orderEntityMapper.toEntity(order)).thenReturn(entityWithDishes);
        when(orderRepository.save(entityWithDishes)).thenReturn(entityWithDishes);
        when(dishRepository.findAllById(Set.of(100L))).thenReturn(List.of(sampleDishEntity));
        when(dishEntityMapper.toDomain(sampleDishEntity)).thenReturn(sampleDish);

        // Act
        Order result = orderAdapterRepository.save(order);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrderDishes().size());
        assertEquals(sampleDish, result.getOrderDishes().get(0).getDish());
        verify(dishRepository, times(1)).findAllById(Set.of(100L));
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
    @DisplayName("Should return paginated orders filtered by restaurant and status")
    void shouldReturnPaginatedOrdersByRestaurantAndStatus() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        OrderDishEntity orderDishEntity = new OrderDishEntity(1L, orderEntity, 100L, 2);
        orderEntity.setOrderDishes(List.of(orderDishEntity));

        Page<OrderEntity> pagedEntities = new PageImpl<>(List.of(orderEntity), pageable, 1);

        when(orderRepository.findByIdRestaurantAndStatus(1L, OrderStatus.PENDIENTE, pageable)).thenReturn(pagedEntities);
        when(dishRepository.findAllById(Set.of(100L))).thenReturn(List.of(sampleDishEntity));
        when(dishEntityMapper.toDomain(sampleDishEntity)).thenReturn(sampleDish);

        // Act
        CustomPage<Order> result = orderAdapterRepository.getOrdersByRestaurantAndStatus(1L, OrderStatus.PENDIENTE, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(sampleDish, result.getContent().get(0).getOrderDishes().get(0).getDish());
        verify(dishRepository, times(1)).findAllById(Set.of(100L));
    }

    @Test
    @DisplayName("Should return empty custom page when no orders match criteria")
    void shouldReturnEmptyCustomPageWhenNoOrdersMatch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(orderRepository.findByIdRestaurantAndStatus(1L, OrderStatus.PENDIENTE, pageable)).thenReturn(emptyPage);

        // Act
        CustomPage<Order> result = orderAdapterRepository.getOrdersByRestaurantAndStatus(1L, OrderStatus.PENDIENTE, 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        verify(dishRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("Should return order when found by id and load dish details")
    void shouldReturnOrderWhenFoundById() {
        // Arrange
        OrderDishEntity orderDishEntity = new OrderDishEntity(1L, orderEntity, 100L, 2);
        orderEntity.setOrderDishes(List.of(orderDishEntity));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(dishRepository.findAllById(Set.of(100L))).thenReturn(List.of(sampleDishEntity));
        when(dishEntityMapper.toDomain(sampleDishEntity)).thenReturn(sampleDish);

        // Act
        Optional<Order> result = orderAdapterRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(sampleDish, result.get().getOrderDishes().get(0).getDish());
        verify(orderRepository, times(1)).findById(1L);
        verify(dishRepository, times(1)).findAllById(Set.of(100L));
    }

    @Test
    @DisplayName("Should return empty optional when order not found by id")
    void shouldReturnEmptyWhenNotFoundById() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Order> result = orderAdapterRepository.findById(999L);

        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById(999L);
        verify(dishRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("Should return all orders with loaded dish details")
    void shouldReturnAllOrders() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(List.of(orderEntity));

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
        orderAdapterRepository.delete(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return order as-is when order has null dishes list")
    void shouldReturnOrderAsIsWhenDishesListIsNull() {
        // Arrange
        orderEntity.setOrderDishes(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));

        // Act
        Optional<Order> result = orderAdapterRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getOrderDishes().isEmpty());
        verify(dishRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("Should return order as-is when all dish ids are null")
    void shouldReturnOrderAsIsWhenAllDishIdsAreNull() {
        // Arrange
        OrderDishEntity orderDishEntity = new OrderDishEntity(1L, null, null, 2);
        orderEntity.setOrderDishes(List.of(orderDishEntity));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));

        // Act
        Optional<Order> result = orderAdapterRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getOrderDishes().size());
        assertNull(result.get().getOrderDishes().get(0).getDish());
        verify(dishRepository, never()).findAllById(any());
    }
}