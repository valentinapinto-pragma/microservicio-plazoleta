package com.pragma.plazoleta.infrastructure.client.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TraceabilityRequestDtoTest {

    @Test
    @DisplayName("Should create TraceabilityRequestDto with all-args constructor and getters")
    void shouldCreateTraceabilityRequestDtoWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        TraceabilityRequestDto dto = new TraceabilityRequestDto(
                1L,
                4L,
                "cliente@correo.com",
                now,
                "PENDIENTE",
                "EN_PREPARACION",
                3L,
                "empleado@correo.com"
        );

        assertEquals(1L, dto.getOrderId());
        assertEquals(4L, dto.getClientId());
        assertEquals("cliente@correo.com", dto.getClientEmail());
        assertEquals(now, dto.getDate());
        assertEquals("PENDIENTE", dto.getPreviousStatus());
        assertEquals("EN_PREPARACION", dto.getNewStatus());
        assertEquals(3L, dto.getEmployeeId());
        assertEquals("empleado@correo.com", dto.getEmployeeEmail());
    }

    @Test
    @DisplayName("Should set and get all fields using no-args constructor and setters")
    void shouldSetAndGetAllFields() {
        TraceabilityRequestDto dto = new TraceabilityRequestDto();
        LocalDateTime now = LocalDateTime.now();

        dto.setOrderId(1L);
        dto.setClientId(4L);
        dto.setClientEmail("cliente@correo.com");
        dto.setDate(now);
        dto.setPreviousStatus(null);
        dto.setNewStatus("PENDIENTE");
        dto.setEmployeeId(null);
        dto.setEmployeeEmail(null);

        assertEquals(1L, dto.getOrderId());
        assertEquals(4L, dto.getClientId());
        assertEquals("cliente@correo.com", dto.getClientEmail());
        assertEquals(now, dto.getDate());
        assertNull(dto.getPreviousStatus());
        assertEquals("PENDIENTE", dto.getNewStatus());
        assertNull(dto.getEmployeeId());
        assertNull(dto.getEmployeeEmail());
    }
}