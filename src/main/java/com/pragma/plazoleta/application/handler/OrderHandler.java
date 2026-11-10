package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderStatusRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.dto.response.PaginationResponseDto;
import com.pragma.plazoleta.application.mapper.OrderDtoMapper;
import com.pragma.plazoleta.domain.model.CustomPage;
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

    public void createOrder(OrderRequestDto orderRequestDto) {
        Order order = orderDtoMapper.toDomain(orderRequestDto);
        orderServicePort.createOrder(order);
    }

    @Transactional(readOnly = true)
    public PaginationResponseDto<OrderResponseDto> getOrdersByStatus(OrderStatus status, int page, int size) {
        CustomPage<Order> ordersPage = orderServicePort.getOrdersByStatus(status, page, size);
        List<OrderResponseDto> responseList = orderDtoMapper.toResponseDtoList(ordersPage.getContent());
        return new PaginationResponseDto<>(
                responseList,
                ordersPage.getCurrentPage(),
                ordersPage.getPageSize(),
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages()
        );
    }

    public OrderResponseDto assignOrder(Long id, OrderStatusRequestDto orderStatusRequestDto) {
        Order updatedOrder = orderServicePort.assignOrder(id, orderStatusRequestDto.getStatus());
        return orderDtoMapper.toResponseDto(updatedOrder);
    }

    public OrderResponseDto updateOrderToReady(Long id) {
        Order updatedOrder = orderServicePort.updateOrderToReady(id);
        return orderDtoMapper.toResponseDto(updatedOrder);
    }
}