package com.pragma.plazoleta.infrastructure.persistence.entity;

import com.pragma.plazoleta.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityTest {

    @Test
    @DisplayName("Should test OrderEntity constructors, getters and setters")
    void testOrderEntity() {
        LocalDateTime now = LocalDateTime.now();
        OrderEntity entity = new OrderEntity(1L, 10L, now, OrderStatus.PENDIENTE, 5L, 2L, new ArrayList<>());

        assertEquals(1L, entity.getId());
        assertEquals(10L, entity.getIdClient());
        assertEquals(now, entity.getDate());
        assertEquals(OrderStatus.PENDIENTE, entity.getStatus());
        assertEquals(5L, entity.getIdChef());
        assertEquals(2L, entity.getIdRestaurant());
        assertNotNull(entity.getOrderDishes());

        OrderEntity empty = new OrderEntity();
        empty.setId(2L);
        empty.setStatus(OrderStatus.EN_PREPARACION);
        assertEquals(2L, empty.getId());
        assertEquals(OrderStatus.EN_PREPARACION, empty.getStatus());
    }
}