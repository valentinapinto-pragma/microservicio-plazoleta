package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderStatusRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.mapper.OrderDtoMapper;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.servicePort.OrderServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
public class OrderHandler {

    private final OrderServicePort orderServicePort;
    private final OrderDtoMapper orderDtoMapper;

    public void createOrder(OrderRequestDto orderRequestDto, Long clientId) {
        Order order = orderDtoMapper.toDomain(orderRequestDto);
        order.setClientId(clientId);
        orderServicePort.createOrder(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus status, int page, int size, Long employeeId) {
        List<Order> orders = orderServicePort.getOrdersByStatus(status, page, size, employeeId);
        return orderDtoMapper.toResponseDtoList(orders);
    }

    public OrderResponseDto assignOrder(Long orderId, OrderStatusRequestDto requestDto, Long employeeId) {
        Order updatedOrder = orderServicePort.assignOrder(orderId, requestDto.getStatus(), employeeId);
        return orderDtoMapper.toResponseDto(updatedOrder);
    }

    public OrderResponseDto updateOrderToReady(Long orderId, Long employeeId) {
        Order updatedOrder = orderServicePort.updateOrderToReady(orderId, employeeId);
        return orderDtoMapper.toResponseDto(updatedOrder);
    }
}