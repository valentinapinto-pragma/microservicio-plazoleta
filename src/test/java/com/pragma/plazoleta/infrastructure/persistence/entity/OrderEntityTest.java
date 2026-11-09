package com.pragma.plazoleta.infrastructure.persistence.entity;

import com.pragma.plazoleta.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderEntityTest {

    @Test
    @DisplayName("Should test OrderEntity getters, setters and constructors")
    void shouldTestGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        OrderEntity entity = new OrderEntity(1L, 10L, now, OrderStatus.PENDIENTE, 5L, 2L, new ArrayList<>());

        assertEquals(1L, entity.getId());
        assertEquals(10L, entity.getIdClient());
        assertEquals(now, entity.getDate());
        assertEquals(OrderStatus.PENDIENTE, entity.getStatus());
        assertEquals(5L, entity.getIdChef());
        assertEquals(2L, entity.getIdRestaurant());
        assertEquals(0, entity.getOrderDishes().size());
        assertNull(entity.getSecurityPin());

        OrderEntity empty = new OrderEntity();
        empty.setId(2L);
        empty.setIdClient(20L);
        empty.setDate(now);
        empty.setStatus(OrderStatus.EN_PREPARACION);
        empty.setIdChef(6L);
        empty.setIdRestaurant(3L);
        empty.setOrderDishes(new ArrayList<>());
        empty.setSecurityPin("999888");

        assertEquals(2L, empty.getId());
        assertEquals(20L, empty.getIdClient());
        assertEquals(now, empty.getDate());
        assertEquals(OrderStatus.EN_PREPARACION, empty.getStatus());
        assertEquals(6L, empty.getIdChef());
        assertEquals(3L, empty.getIdRestaurant());
        assertEquals(0, empty.getOrderDishes().size());
        assertEquals("999888", empty.getSecurityPin());
    }
}