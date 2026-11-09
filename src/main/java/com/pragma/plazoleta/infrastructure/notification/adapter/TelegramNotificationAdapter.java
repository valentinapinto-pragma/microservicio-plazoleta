package com.pragma.plazoleta.infrastructure.notification.adapter;

import com.pragma.plazoleta.domain.repositoryPort.MessageNotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TelegramNotificationAdapter implements MessageNotificationPort {

    private final RestTemplate restTemplate;
    private final String botToken;
    private final String defaultChatId;

    public TelegramNotificationAdapter(RestTemplate restTemplate,
                                       @Value("${telegram.bot.token:9061725449:AAF6beBpDha8Dwb7Y5P1amfErmQSMD3fLY8}") String botToken,
                                       @Value("${telegram.bot.chat-id:1833923415}") String defaultChatId) {
        this.restTemplate = restTemplate;
        this.botToken = botToken;
        this.defaultChatId = defaultChatId;
    }

    @Override
    public void sendMessage(String destination, String message) {
        String targetChatId = (destination != null && destination.matches("\\d{8,12}")) ? destination : defaultChatId;
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("chat_id", targetChatId);
            body.put("text", message);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, request, String.class);
            log.info("Notificación enviada exitosamente por Telegram a chatId: {}", targetChatId);
        } catch (Exception e) {
            log.error("Error al enviar notificación por Telegram: {}", e.getMessage());
        }
    }
}