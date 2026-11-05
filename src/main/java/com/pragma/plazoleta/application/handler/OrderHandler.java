package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.mapper.OrderDtoMapper;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.servicePort.OrderServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

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
}