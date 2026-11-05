package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.OrderDishRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.mapper.OrderDtoMapper;
import com.pragma.plazoleta.domain.model.Order;
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
    @DisplayName("Should successfully map DTO, set client ID and call order service port")
    void shouldCreateOrderSuccessfully() {
        // Arrange
        OrderDishRequestDto dishDto = new OrderDishRequestDto(100L, 2);
        OrderRequestDto requestDto = new OrderRequestDto(1L, List.of(dishDto));

        Order domainOrder = new Order(null, null, null, null, null, 1L, new ArrayList<>());

        when(orderDtoMapper.toDomain(requestDto)).thenReturn(domainOrder);

        // Act
        orderHandler.createOrder(requestDto, 10L);

        // Assert
        assertEquals(10L, domainOrder.getClientId());
        verify(orderDtoMapper, times(1)).toDomain(requestDto);
        verify(orderServicePort, times(1)).createOrder(domainOrder);
    }
}