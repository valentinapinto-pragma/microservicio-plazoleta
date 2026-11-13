package com.pragma.plazoleta.infrastructure.client.feign;

import com.pragma.plazoleta.infrastructure.client.dto.MessageRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "messaging-service", url = "${services.messaging.url}")
public interface MessagingFeignClient {

    @PostMapping
    void sendMessage(@RequestBody MessageRequestDto messageRequestDto);
}