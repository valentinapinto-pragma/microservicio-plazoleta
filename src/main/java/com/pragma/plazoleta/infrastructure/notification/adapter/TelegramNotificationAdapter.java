package com.pragma.plazoleta.infrastructure.notification.adapter;

import com.pragma.plazoleta.domain.repositoryPort.MessageNotificationPort;
import com.pragma.plazoleta.infrastructure.client.dto.TelegramMessageRequestDto;
import com.pragma.plazoleta.infrastructure.client.feign.TelegramFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TelegramNotificationAdapter implements MessageNotificationPort {

    private final TelegramFeignClient telegramFeignClient;
    private final String botToken;
    private final String defaultChatId;

    public TelegramNotificationAdapter(
            TelegramFeignClient telegramFeignClient,
            @Value("${telegram.bot.token:}") String botToken,
            @Value("${telegram.bot.chat-id:}") String defaultChatId
    ) {
        this.telegramFeignClient = telegramFeignClient;
        this.botToken = botToken;
        this.defaultChatId = defaultChatId;
    }

    @Override
    public void sendMessage(String destination, String message) {
        if (botToken == null || botToken.isBlank()) {
            log.warn("Token de Telegram no configurado. Notificación simulada a {}: {}", destination, message);
            return;
        }

        TelegramMessageRequestDto requestDto = new TelegramMessageRequestDto(defaultChatId, message);

        try {
            telegramFeignClient.sendMessage(botToken, requestDto);
            log.info("Notificación enviada exitosamente por Telegram a chatId: {} (destinatario original: {})", defaultChatId, destination);
        } catch (Exception e) {
            log.error("Error al enviar notificación por Telegram a {}: {}", defaultChatId, e.getMessage());
        }
    }
}