package com.pragma.plazoleta.infrastructure.notification.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    private TelegramNotificationAdapter adapter;

    private final String botToken = "test-token";
    private final String defaultChatId = "1833923415";

    @BeforeEach
    void setUp() {
        adapter = new TelegramNotificationAdapter(restTemplate, botToken, defaultChatId);
    }

    @Test
    @DisplayName("Should send message using destination chatId when destination is valid chatId format")
    void shouldSendMessageWithDestinationChatId() {
        String destination = "123456789";
        String message = "Tu pedido está listo. PIN: 123456";

        when(restTemplate.postForEntity(eq("https://api.telegram.org/bottest-token/sendMessage"), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        adapter.sendMessage(destination, message);

        ArgumentCaptor<HttpEntity<Map<String, String>>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate, times(1)).postForEntity(eq("https://api.telegram.org/bottest-token/sendMessage"), captor.capture(), eq(String.class));

        Map<String, String> body = captor.getValue().getBody();
        assertNotNull(body);
        assertEquals("123456789", body.get("chat_id"));
        assertEquals(message, body.get("text"));
    }

    @Test
    @DisplayName("Should send message using defaultChatId when destination is not a direct telegram chatId")
    void shouldSendMessageWithDefaultChatIdWhenDestinationIsPhoneOrNull() {
        String destination = "+573001234567";
        String message = "Tu pedido está listo. PIN: 654321";

        when(restTemplate.postForEntity(eq("https://api.telegram.org/bottest-token/sendMessage"), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        adapter.sendMessage(destination, message);

        ArgumentCaptor<HttpEntity<Map<String, String>>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate, times(1)).postForEntity(anyString(), captor.capture(), eq(String.class));

        Map<String, String> body = captor.getValue().getBody();
        assertNotNull(body);
        assertEquals(defaultChatId, body.get("chat_id"));
    }

    @Test
    @DisplayName("Should handle RestClientException gracefully without crashing execution")
    void shouldHandleExceptionGracefully() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Telegram API timeout"));

        assertDoesNotThrow(() -> adapter.sendMessage(null, "Test error handling"));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }
}