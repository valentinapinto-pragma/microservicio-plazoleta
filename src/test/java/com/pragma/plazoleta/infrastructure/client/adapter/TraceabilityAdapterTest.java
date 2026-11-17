package com.pragma.plazoleta.infrastructure.client.adapter;

import com.pragma.plazoleta.domain.model.OrderTraceLog;
import com.pragma.plazoleta.infrastructure.client.dto.TraceabilityRequestDto;
import com.pragma.plazoleta.infrastructure.client.feign.TraceabilityFeignClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraceabilityAdapterTest {

    @Mock
    private TraceabilityFeignClient traceabilityFeignClient;

    @InjectMocks
    private TraceabilityAdapter traceabilityAdapter;

    @Test
    @DisplayName("Should successfully send trace to FeignClient when OrderTraceLog is valid")
    void shouldSaveOrderTraceSuccessfully() {
        LocalDateTime now = LocalDateTime.now();
        OrderTraceLog traceLog = new OrderTraceLog(
                1L,
                4L,
                "cliente@correo.com",
                now,
                null,
                "PENDIENTE",
                null,
                null,
                10L,
                2L
        );

        traceabilityAdapter.saveOrderTrace(traceLog);

        ArgumentCaptor<TraceabilityRequestDto> captor = ArgumentCaptor.forClass(TraceabilityRequestDto.class);
        verify(traceabilityFeignClient, times(1)).saveOrderTrace(captor.capture());

        TraceabilityRequestDto captured = captor.getValue();
        assertEquals(1L, captured.getOrderId());
        assertEquals(4L, captured.getClientId());
        assertEquals("cliente@correo.com", captured.getClientEmail());
        assertEquals("PENDIENTE", captured.getNewStatus());
        assertNull(captured.getPreviousStatus());
        assertEquals(10L, captured.getRestaurantId());
        assertEquals(2L, captured.getRestaurantOwnerId());
    }

    @Test
    @DisplayName("Should do nothing when OrderTraceLog is null")
    void shouldDoNothingWhenOrderTraceLogIsNull() {
        traceabilityAdapter.saveOrderTrace(null);
        verify(traceabilityFeignClient, never()).saveOrderTrace(any());
    }

    @Test
    @DisplayName("Should catch and log exception without rethrowing when FeignClient fails")
    void shouldHandleExceptionWhenFeignClientFails() {
        OrderTraceLog traceLog = new OrderTraceLog(
                1L,
                4L,
                "cliente@correo.com",
                LocalDateTime.now(),
                null,
                "PENDIENTE",
                null,
                null,
                10L,
                2L
        );

        doThrow(new RuntimeException("Connection refused")).when(traceabilityFeignClient).saveOrderTrace(any());

        assertDoesNotThrow(() -> traceabilityAdapter.saveOrderTrace(traceLog));
        verify(traceabilityFeignClient, times(1)).saveOrderTrace(any());
    }
}