package com.pragma.plazoleta.application.dto.request;

import com.pragma.plazoleta.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderStatusRequestDtoTest {

    @Test
    @DisplayName("Should successfully test getters, setters and constructors")
    void shouldTestGettersSettersAndConstructors() {
        OrderStatusRequestDto dto = new OrderStatusRequestDto();
        assertNull(dto.getStatus());

        dto.setStatus(OrderStatus.EN_PREPARACION);
        assertEquals(OrderStatus.EN_PREPARACION, dto.getStatus());

        OrderStatusRequestDto allArgsDto = new OrderStatusRequestDto(OrderStatus.PENDIENTE);
        assertEquals(OrderStatus.PENDIENTE, allArgsDto.getStatus());
    }
}