package com.pragma.plazoleta.infrastructure.notification.adapter;

import com.pragma.plazoleta.infrastructure.client.dto.MessageRequestDto;
import com.pragma.plazoleta.infrastructure.client.feign.MessagingFeignClient;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingNotificationAdapterTest {

    @Mock
    private MessagingFeignClient messagingFeignClient;

    private MessagingNotificationAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new MessagingNotificationAdapter(messagingFeignClient);
    }

    @Test
    @DisplayName("Should send message successfully through feign client")
    void shouldSendMessageSuccessfully() {
        doNothing().when(messagingFeignClient).sendMessage(any(MessageRequestDto.class));

        adapter.sendMessage("+573001234567", "Tu pedido está listo");

        ArgumentCaptor<MessageRequestDto> captor = ArgumentCaptor.forClass(MessageRequestDto.class);
        verify(messagingFeignClient, times(1)).sendMessage(captor.capture());
        assertEquals("+573001234567", captor.getValue().getDestination());
        assertEquals("Tu pedido está listo", captor.getValue().getMessage());
    }

    @Test
    @DisplayName("Should catch and handle exception when feign client fails without throwing")
    void shouldHandleExceptionWhenFeignFails() {
        Request request = Request.create(Request.HttpMethod.POST, "/messages", Collections.emptyMap(), null, new RequestTemplate());
        doThrow(new FeignException.BadRequest("Error", request, null, null))
                .when(messagingFeignClient).sendMessage(any(MessageRequestDto.class));

        assertDoesNotThrow(() -> adapter.sendMessage("+573001234567", "Tu pedido está listo"));
        verify(messagingFeignClient, times(1)).sendMessage(any(MessageRequestDto.class));
    }
}

