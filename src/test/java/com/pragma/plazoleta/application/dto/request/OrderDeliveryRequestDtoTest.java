package com.pragma.plazoleta.application.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderDeliveryRequestDtoTest {

    @Test
    @DisplayName("Should create OrderDeliveryRequestDto with all-args and no-args constructor")
    void shouldCreateOrderDeliveryRequestDto() {
        OrderDeliveryRequestDto dto = new OrderDeliveryRequestDto("123456");
        assertEquals("123456", dto.getSecurityPin());

        OrderDeliveryRequestDto emptyDto = new OrderDeliveryRequestDto();
        emptyDto.setSecurityPin("654321");
        assertEquals("654321", emptyDto.getSecurityPin());
    }
}