package com.pragma.plazoleta.domain.servicePort;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;

import java.util.List;

public interface OrderServicePort {
    Order createOrder(Order order);
    List<Order> getOrdersByStatus(OrderStatus status, int page, int size);
    Order assignOrder(Long orderId, OrderStatus status);
    Order updateOrderToReady(Long orderId);
}