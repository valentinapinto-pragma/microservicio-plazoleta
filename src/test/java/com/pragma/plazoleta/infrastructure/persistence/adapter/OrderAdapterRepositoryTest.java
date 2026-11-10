package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.infrastructure.persistence.entity.OrderEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.OrderEntityMapper;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderAdapterRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntityMapper orderEntityMapper;

    @InjectMocks
    private OrderAdapterRepository orderAdapterRepository;

    private Order order;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        order = new Order(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, new ArrayList<>());
        orderEntity = new OrderEntity(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, new ArrayList<>());
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
    @DisplayName("Should return order when found by id")
    void shouldReturnOrderWhenFoundById() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);

        // Act
        Optional<Order> result = orderAdapterRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return all orders")
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
}