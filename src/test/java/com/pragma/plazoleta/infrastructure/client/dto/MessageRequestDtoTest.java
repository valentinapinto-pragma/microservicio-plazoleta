package com.pragma.plazoleta.infrastructure.client.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageRequestDtoTest {

    @Test
    @DisplayName("Should test MessageRequestDto getters, setters and constructors")
    void shouldTestDtoProperties() {
        MessageRequestDto dto = new MessageRequestDto("+573001234567", "Hello");
        assertEquals("+573001234567", dto.getDestination());
        assertEquals("Hello", dto.getMessage());

        MessageRequestDto empty = new MessageRequestDto();
        empty.setDestination("+573009999999");
        empty.setMessage("World");
        assertEquals("+573009999999", empty.getDestination());
        assertEquals("World", empty.getMessage());
    }
}

