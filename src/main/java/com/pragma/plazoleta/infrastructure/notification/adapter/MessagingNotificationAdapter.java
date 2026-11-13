package com.pragma.plazoleta.infrastructure.notification.adapter;

import com.pragma.plazoleta.domain.repositoryPort.MessageNotificationPort;
import com.pragma.plazoleta.infrastructure.client.dto.MessageRequestDto;
import com.pragma.plazoleta.infrastructure.client.feign.MessagingFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessagingNotificationAdapter implements MessageNotificationPort {

    private final MessagingFeignClient messagingFeignClient;

    @Override
    public void sendMessage(String destination, String message) {
        MessageRequestDto requestDto = new MessageRequestDto(destination, message);
        try {
            messagingFeignClient.sendMessage(requestDto);
            log.info("Notificación enviada exitosamente a través de microservicio-mensajeria a {}", destination);
        } catch (Exception e) {
            log.error("Error al enviar notificación a microservicio-mensajeria para {}: {}", destination, e.getMessage());
        }
    }
}

