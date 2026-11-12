package com.pragma.plazoleta.infrastructure.client.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelegramMessageRequestDtoTest {

    @Test
    @DisplayName("Should create TelegramMessageRequestDto using constructor and builder")
    void shouldCreateTelegramMessageRequestDto() {
        TelegramMessageRequestDto dto = new TelegramMessageRequestDto("123456", "Hello");
        assertEquals("123456", dto.getChatId());
        assertEquals("Hello", dto.getText());

        TelegramMessageRequestDto noArgs = new TelegramMessageRequestDto();
        noArgs.setChatId("654321");
        noArgs.setText("World");
        assertEquals("654321", noArgs.getChatId());
        assertEquals("World", noArgs.getText());

        TelegramMessageRequestDto built = TelegramMessageRequestDto.builder()
                .chatId("999999")
                .text("Test")
                .build();
        assertEquals("999999", built.getChatId());
        assertEquals("Test", built.getText());
        assertNotNull(TelegramMessageRequestDto.builder().chatId("1").text("T").toString());
    }
}