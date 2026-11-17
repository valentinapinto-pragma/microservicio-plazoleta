package com.pragma.plazoleta.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTraceLogTest {

    @Test
    @DisplayName("Should create OrderTraceLog using all-args constructor and verify getters")
    void shouldCreateOrderTraceLogWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        OrderTraceLog traceLog = new OrderTraceLog(
                1L,
                4L,
                "cliente@correo.com",
                now,
                null,
                "PENDIENTE",
                3L,
                "empleado@correo.com",
                10L,
                2L
        );

        assertEquals(1L, traceLog.getOrderId());
        assertEquals(4L, traceLog.getClientId());
        assertEquals("cliente@correo.com", traceLog.getClientEmail());
        assertEquals(now, traceLog.getDate());
        assertNull(traceLog.getPreviousStatus());
        assertEquals("PENDIENTE", traceLog.getNewStatus());
        assertEquals(3L, traceLog.getEmployeeId());
        assertEquals("empleado@correo.com", traceLog.getEmployeeEmail());
        assertEquals(10L, traceLog.getRestaurantId());
        assertEquals(2L, traceLog.getRestaurantOwnerId());
    }

    @Test
    @DisplayName("Should set and get all fields using no-args constructor and setters")
    void shouldSetAndGetAllFields() {
        OrderTraceLog traceLog = new OrderTraceLog();
        LocalDateTime now = LocalDateTime.now();

        traceLog.setOrderId(10L);
        traceLog.setClientId(20L);
        traceLog.setClientEmail("test@correo.com");
        traceLog.setDate(now);
        traceLog.setPreviousStatus("PENDIENTE");
        traceLog.setNewStatus("EN_PREPARACION");
        traceLog.setEmployeeId(30L);
        traceLog.setEmployeeEmail("chef@correo.com");
        traceLog.setRestaurantId(1L);
        traceLog.setRestaurantOwnerId(2L);

        assertEquals(10L, traceLog.getOrderId());
        assertEquals(20L, traceLog.getClientId());
        assertEquals("test@correo.com", traceLog.getClientEmail());
        assertEquals(now, traceLog.getDate());
        assertEquals("PENDIENTE", traceLog.getPreviousStatus());
        assertEquals("EN_PREPARACION", traceLog.getNewStatus());
        assertEquals(30L, traceLog.getEmployeeId());
        assertEquals("chef@correo.com", traceLog.getEmployeeEmail());
        assertEquals(1L, traceLog.getRestaurantId());
        assertEquals(2L, traceLog.getRestaurantOwnerId());
    }
}