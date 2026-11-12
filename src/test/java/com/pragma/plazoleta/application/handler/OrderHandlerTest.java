package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.OrderDeliveryRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderDishRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.dto.response.PaginationResponseDto;
import com.pragma.plazoleta.application.mapper.OrderDtoMapper;
import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.servicePort.OrderServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderHandlerTest {

    @Mock
    private OrderServicePort orderServicePort;

    @Mock
    private OrderDtoMapper orderDtoMapper;

    @InjectMocks
    private OrderHandler orderHandler;

    @Test
    @DisplayName("Should successfully map DTO and call order service port")
    void shouldCreateOrderSuccessfully() {
        // Arrange
        OrderDishRequestDto dishDto = new OrderDishRequestDto(100L, 2);
        OrderRequestDto requestDto = new OrderRequestDto(1L, List.of(dishDto));

        Order domainOrder = new Order(null, null, null, null, null, 1L, new ArrayList<>());

        when(orderDtoMapper.toDomain(requestDto)).thenReturn(domainOrder);

        // Act
        orderHandler.createOrder(requestDto);

        // Assert
        verify(orderDtoMapper, times(1)).toDomain(requestDto);
        verify(orderServicePort, times(1)).createOrder(domainOrder);
    }

    @Test
    @DisplayName("Should get orders by status through service port and map to pagination response DTO")
    void shouldGetOrdersByStatusSuccessfully() {
        OrderStatus status = OrderStatus.PENDIENTE;
        int page = 0;
        int size = 10;

        Order order = new Order(1L, 10L, null, status, null, 2L, List.of());
        OrderResponseDto responseDto = new OrderResponseDto(1L, 10L, null, status, null, 2L, List.of());
        CustomPage<Order> domainPage = new CustomPage<>(List.of(order), page, size, 1L, 1);

        when(orderServicePort.getOrdersByStatus(status, page, size)).thenReturn(domainPage);
        when(orderDtoMapper.toResponseDtoList(List.of(order))).thenReturn(List.of(responseDto));

        // Act
        PaginationResponseDto<OrderResponseDto> result = orderHandler.getOrdersByStatus(status, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(page, result.getCurrentPage());
        assertEquals(size, result.getPageSize());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(orderServicePort, times(1)).getOrdersByStatus(status, page, size);
        verify(orderDtoMapper, times(1)).toResponseDtoList(List.of(order));
    }

    @Test
    @DisplayName("Should assign order through service port and map to response DTO")
    void shouldAssignOrderSuccessfully() {
        // Arrange
        Long orderId = 100L;

        Order updatedOrder = new Order(orderId, 10L, null, OrderStatus.EN_PREPARACION, 3L, 1L, List.of());
        OrderResponseDto responseDto = new OrderResponseDto(orderId, 10L, null, OrderStatus.EN_PREPARACION, 3L, 1L, List.of());

        when(orderServicePort.assignOrder(orderId)).thenReturn(updatedOrder);
        when(orderDtoMapper.toResponseDto(updatedOrder)).thenReturn(responseDto);

        // Act
        OrderResponseDto result = orderHandler.assignOrder(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(OrderStatus.EN_PREPARACION, result.getStatus());
        assertEquals(3L, result.getIdChef());
        verify(orderServicePort, times(1)).assignOrder(orderId);
        verify(orderDtoMapper, times(1)).toResponseDto(updatedOrder);
    }

    @Test
    @DisplayName("Should update order to ready through service port and map to response DTO")
    void shouldUpdateOrderToReadySuccessfully() {
        Long orderId = 100L;
        Order readyOrder = new Order(orderId, 10L, null, OrderStatus.LISTO, 3L, 1L, List.of(), "123456");
        OrderResponseDto responseDto = new OrderResponseDto(orderId, 10L, null, OrderStatus.LISTO, 3L, 1L, List.of());

        when(orderServicePort.updateOrderToReady(orderId)).thenReturn(readyOrder);
        when(orderDtoMapper.toResponseDto(readyOrder)).thenReturn(responseDto);

        // Act
        OrderResponseDto result = orderHandler.updateOrderToReady(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(OrderStatus.LISTO, result.getStatus());
        verify(orderServicePort, times(1)).updateOrderToReady(orderId);
        verify(orderDtoMapper, times(1)).toResponseDto(readyOrder);
    }

    @Test
    @DisplayName("Should deliver order through service port and map to response DTO")
    void shouldDeliverOrderSuccessfully() {
        // Arrange
        Long orderId = 100L;
        String pin = "123456";
        OrderDeliveryRequestDto deliveryDto = new OrderDeliveryRequestDto(pin);
        Order deliveredOrder = new Order(orderId, 10L, null, OrderStatus.ENTREGADO, 3L, 1L, List.of());
        OrderResponseDto responseDto = new OrderResponseDto(orderId, 10L, null, OrderStatus.ENTREGADO, 3L, 1L, List.of());

        when(orderServicePort.deliverOrder(orderId, pin)).thenReturn(deliveredOrder);
        when(orderDtoMapper.toResponseDto(deliveredOrder)).thenReturn(responseDto);

        // Act
        OrderResponseDto result = orderHandler.deliverOrder(orderId, deliveryDto);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(OrderStatus.ENTREGADO, result.getStatus());
        verify(orderServicePort, times(1)).deliverOrder(orderId, pin);
        verify(orderDtoMapper, times(1)).toResponseDto(deliveredOrder);
    }
}