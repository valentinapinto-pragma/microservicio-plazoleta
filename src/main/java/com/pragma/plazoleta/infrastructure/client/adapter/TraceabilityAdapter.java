package com.pragma.plazoleta.infrastructure.client.adapter;

import com.pragma.plazoleta.domain.model.OrderTraceLog;
import com.pragma.plazoleta.domain.repositoryPort.OrderTraceabilityPort;
import com.pragma.plazoleta.infrastructure.client.dto.TraceabilityRequestDto;
import com.pragma.plazoleta.infrastructure.client.feign.TraceabilityFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TraceabilityAdapter implements OrderTraceabilityPort {

    private final TraceabilityFeignClient traceabilityFeignClient;

    @Override
    public void saveOrderTrace(OrderTraceLog orderTraceLog) {
        if (orderTraceLog == null) {
            return;
        }
        try {
            TraceabilityRequestDto requestDto = new TraceabilityRequestDto(
                    orderTraceLog.getOrderId(),
                    orderTraceLog.getClientId(),
                    orderTraceLog.getClientEmail(),
                    orderTraceLog.getDate(),
                    orderTraceLog.getPreviousStatus(),
                    orderTraceLog.getNewStatus(),
                    orderTraceLog.getEmployeeId(),
                    orderTraceLog.getEmployeeEmail(),
                    orderTraceLog.getRestaurantId(),
                    orderTraceLog.getRestaurantOwnerId()
            );
            traceabilityFeignClient.saveOrderTrace(requestDto);
            log.info("Traza registrada exitosamente en microservicio-trazabilidad para pedido {}", orderTraceLog.getOrderId());
        } catch (Exception e) {
            log.error("Error al registrar traza en microservicio-trazabilidad para pedido {}: {}",
                    orderTraceLog.getOrderId(), e.getMessage());
        }
    }
}