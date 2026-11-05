package com.pragma.plazoleta.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderDishEntityTest {

    @Test
    @DisplayName("Should test OrderDishEntity constructors, getters and setters")
    void testOrderDishEntity() {
        OrderEntity order = new OrderEntity();
        order.setId(10L);

        OrderDishEntity entity = new OrderDishEntity(1L, order, 100L, 3);

        assertEquals(1L, entity.getId());
        assertEquals(order, entity.getOrder());
        assertEquals(100L, entity.getIdDish());
        assertEquals(3, entity.getQuantity());

        OrderDishEntity empty = new OrderDishEntity();
        empty.setQuantity(5);
        assertEquals(5, empty.getQuantity());
    }
}