package com.pragma.plazoleta.infrastructure.client.feign;

import com.pragma.plazoleta.infrastructure.client.dto.TraceabilityRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "traceability-service", url = "${services.traceability.url}")
public interface TraceabilityFeignClient {

    @PostMapping
    void saveOrderTrace(@RequestBody TraceabilityRequestDto requestDto);
}