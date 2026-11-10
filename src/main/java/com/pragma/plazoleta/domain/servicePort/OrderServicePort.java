package com.pragma.plazoleta.domain.servicePort;

import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;

public interface OrderServicePort {
    Order createOrder(Order order);
    CustomPage<Order> getOrdersByStatus(OrderStatus status, int page, int size);
    Order assignOrder(Long orderId, OrderStatus status);
    Order updateOrderToReady(Long orderId);
}