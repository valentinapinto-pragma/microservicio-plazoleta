package com.pragma.plazoleta.infrastructure.client.feign;

import com.pragma.plazoleta.infrastructure.client.dto.TelegramMessageRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "telegram-service", url = "${telegram.api.url}")
public interface TelegramFeignClient {

    @PostMapping("/bot{botToken}/sendMessage")
    void sendMessage(@PathVariable("botToken") String botToken, @RequestBody TelegramMessageRequestDto requestDto);
}