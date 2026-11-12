package com.pragma.plazoleta.infrastructure.notification.adapter;

import com.pragma.plazoleta.infrastructure.client.dto.TelegramMessageRequestDto;
import com.pragma.plazoleta.infrastructure.client.feign.TelegramFeignClient;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationAdapterTest {

    @Mock
    private TelegramFeignClient telegramFeignClient;

    private TelegramNotificationAdapter adapter;

    private final String botToken = "test-token";
    private final String defaultChatId = "1833923415";

    @BeforeEach
    void setUp() {
        adapter = new TelegramNotificationAdapter(telegramFeignClient, botToken, defaultChatId);
    }

    @Test
    @DisplayName("Should send message using destination chatId when destination is valid chatId format")
    void shouldSendMessageWithDestinationChatId() {
        String destination = "123456789";
        String message = "Tu pedido está listo. PIN: 123456";

        doNothing().when(telegramFeignClient).sendMessage(eq(botToken), any(TelegramMessageRequestDto.class));

        adapter.sendMessage(destination, message);

        ArgumentCaptor<TelegramMessageRequestDto> captor = ArgumentCaptor.forClass(TelegramMessageRequestDto.class);
        verify(telegramFeignClient, times(1)).sendMessage(eq(botToken), captor.capture());

        TelegramMessageRequestDto captured = captor.getValue();
        assertNotNull(captured);
        assertEquals("123456789", captured.getChatId());
        assertEquals(message, captured.getText());
    }

    @Test
    @DisplayName("Should send message using defaultChatId when destination is not a direct telegram chatId")
    void shouldSendMessageWithDefaultChatIdWhenDestinationIsPhoneOrNull() {
        String destination = "+573001234567";
        String message = "Tu pedido está listo. PIN: 654321";

        doNothing().when(telegramFeignClient).sendMessage(eq(botToken), any(TelegramMessageRequestDto.class));

        adapter.sendMessage(destination, message);

        ArgumentCaptor<TelegramMessageRequestDto> captor = ArgumentCaptor.forClass(TelegramMessageRequestDto.class);
        verify(telegramFeignClient, times(1)).sendMessage(eq(botToken), captor.capture());

        TelegramMessageRequestDto captured = captor.getValue();
        assertNotNull(captured);
        assertEquals(defaultChatId, captured.getChatId());
        assertEquals(message, captured.getText());
    }

    @Test
    @DisplayName("Should simulate notification when botToken is null or blank")
    void shouldSimulateWhenBotTokenIsMissing() {
        TelegramNotificationAdapter unconfiguredAdapter = new TelegramNotificationAdapter(telegramFeignClient, null, defaultChatId);
        assertDoesNotThrow(() -> unconfiguredAdapter.sendMessage("123456789", "Test message"));
        verifyNoInteractions(telegramFeignClient);

        TelegramNotificationAdapter blankTokenAdapter = new TelegramNotificationAdapter(telegramFeignClient, "   ", defaultChatId);
        assertDoesNotThrow(() -> blankTokenAdapter.sendMessage("123456789", "Test message"));
        verifyNoInteractions(telegramFeignClient);
    }

    @Test
    @DisplayName("Should handle FeignException gracefully without crashing execution")
    void shouldHandleExceptionGracefully() {
        Request request = Request.create(Request.HttpMethod.POST, "/sendMessage", Collections.emptyMap(), null, new RequestTemplate());
        doThrow(new FeignException.InternalServerError("Telegram API error", request, null, null))
                .when(telegramFeignClient).sendMessage(eq(botToken), any(TelegramMessageRequestDto.class));

        assertDoesNotThrow(() -> adapter.sendMessage(null, "Test error handling"));
        verify(telegramFeignClient, times(1)).sendMessage(eq(botToken), any(TelegramMessageRequestDto.class));
    }
}