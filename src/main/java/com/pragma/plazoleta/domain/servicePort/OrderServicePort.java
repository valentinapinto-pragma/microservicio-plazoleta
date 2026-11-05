package com.pragma.plazoleta.domain.servicePort;

import com.pragma.plazoleta.domain.model.Order;

public interface OrderServicePort {
    Order createOrder(Order order);
}