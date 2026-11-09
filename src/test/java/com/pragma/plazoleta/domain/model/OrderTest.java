package com.pragma.plazoleta.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("Should create order with no-args constructor and test all getters and setters")
    void shouldTestGettersAndSetters() {
        Order order = new Order();
        LocalDateTime now = LocalDateTime.now();
        List<OrderDish> dishes = new ArrayList<>();

        order.setId(1L);
        order.setClientId(10L);
        order.setDate(now);
        order.setStatus(OrderStatus.EN_PREPARACION);
        order.setChefId(5L);
        order.setRestaurantId(2L);
        order.setOrderDishes(dishes);
        order.setSecurityPin("123456");

        assertEquals(1L, order.getId());
        assertEquals(10L, order.getClientId());
        assertEquals(now, order.getDate());
        assertEquals(OrderStatus.EN_PREPARACION, order.getStatus());
        assertEquals(5L, order.getChefId());
        assertEquals(2L, order.getRestaurantId());
        assertSame(dishes, order.getOrderDishes());
        assertEquals("123456", order.getSecurityPin());
    }

    @Test
    @DisplayName("Should create order with 7-args constructor and default securityPin to null")
    void shouldCreateOrderWith7Args() {
        LocalDateTime now = LocalDateTime.now();
        List<OrderDish> dishes = List.of(new OrderDish(1L, null, 100L, 2));

        Order order = new Order(1L, 10L, now, OrderStatus.PENDIENTE, 5L, 2L, dishes);

        assertEquals(1L, order.getId());
        assertEquals(10L, order.getClientId());
        assertEquals(now, order.getDate());
        assertEquals(OrderStatus.PENDIENTE, order.getStatus());
        assertEquals(5L, order.getChefId());
        assertEquals(2L, order.getRestaurantId());
        assertEquals(dishes, order.getOrderDishes());
        assertNull(order.getSecurityPin());
    }

    @Test
    @DisplayName("Should create order with 8-args constructor including securityPin")
    void shouldCreateOrderWith8Args() {
        LocalDateTime now = LocalDateTime.now();
        List<OrderDish> dishes = List.of(new OrderDish(1L, null, 100L, 2));

        Order order = new Order(1L, 10L, now, OrderStatus.LISTO, 5L, 2L, dishes, "654321");

        assertEquals(1L, order.getId());
        assertEquals(10L, order.getClientId());
        assertEquals(now, order.getDate());
        assertEquals(OrderStatus.LISTO, order.getStatus());
        assertEquals(5L, order.getChefId());
        assertEquals(2L, order.getRestaurantId());
        assertEquals(dishes, order.getOrderDishes());
        assertEquals("654321", order.getSecurityPin());
    }
}