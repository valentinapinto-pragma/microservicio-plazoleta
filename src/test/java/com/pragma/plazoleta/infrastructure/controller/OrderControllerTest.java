package com.pragma.plazoleta.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.OrderDishRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.handler.OrderHandler;
import com.pragma.plazoleta.domain.exception.ClientHasActiveOrderException;
import com.pragma.plazoleta.domain.exception.DishDoesNotBelongToRestaurantException;
import com.pragma.plazoleta.domain.exception.DishInactiveException;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidOrderDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.infrastructure.exceptionhandler.ControllerAdvisor;
import com.pragma.plazoleta.infrastructure.security.JwtAdapter;
import com.pragma.plazoleta.infrastructure.security.SecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({SecurityConfiguration.class, ControllerAdvisor.class})
@WithMockUser(roles = "CLIENT")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderHandler orderHandler;

    @MockBean
    private JwtAdapter jwtAdapter;

    private static final String AUTH_HEADER = "Bearer test-jwt-token";

    @Test
    @DisplayName("Should return 201 Created when order is valid and user is client")
    void shouldReturnCreatedWhenValidOrder() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        when(jwtAdapter.extractUserId(anyString())).thenReturn(5L);
        doNothing().when(orderHandler).createOrder(any(OrderRequestDto.class), eq(5L));

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(jwtAdapter).extractUserId(AUTH_HEADER);
        verify(orderHandler).createOrder(any(OrderRequestDto.class), eq(5L));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    @DisplayName("Should return 403 Forbidden when user is not a client")
    void shouldReturnForbiddenWhenUserIsNotClient() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verify(orderHandler, never()).createOrder(any(), any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when request body is invalid")
    void shouldReturnBadRequestWhenBodyInvalid() throws Exception {
        OrderRequestDto invalidDto = new OrderRequestDto(null, null);

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any(), any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when client already has an active order")
    void shouldReturnBadRequestWhenClientHasActiveOrder() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        when(jwtAdapter.extractUserId(anyString())).thenReturn(5L);
        doThrow(new ClientHasActiveOrderException("The client already has an active order in progress"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class), eq(5L));

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The client already has an active order in progress"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when dish does not belong to the restaurant")
    void shouldReturnBadRequestWhenDishDoesNotBelongToRestaurant() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        when(jwtAdapter.extractUserId(anyString())).thenReturn(5L);
        doThrow(new DishDoesNotBelongToRestaurantException("The dish with id 100 does not belong to restaurant with id 1"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class), eq(5L));

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The dish with id 100 does not belong to restaurant with id 1"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when dish is inactive")
    void shouldReturnBadRequestWhenDishIsInactive() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        when(jwtAdapter.extractUserId(anyString())).thenReturn(5L);
        doThrow(new DishInactiveException("The dish 'Burger' is currently inactive"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class), eq(5L));

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The dish 'Burger' is currently inactive"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when order data is invalid")
    void shouldReturnBadRequestWhenInvalidOrderData() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        when(jwtAdapter.extractUserId(anyString())).thenReturn(5L);
        doThrow(new InvalidOrderDataException("The order must contain at least one dish"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class), eq(5L));

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The order must contain at least one dish"));
    }

    @Test
    @DisplayName("Should return 404 Not Found when dish does not exist")
    void shouldReturnNotFoundWhenDishNotFound() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(999L, 2)));

        when(jwtAdapter.extractUserId(anyString())).thenReturn(5L);
        doThrow(new DishNotFoundException("Dish not found with id: 999"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class), eq(5L));

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Dish not found with id: 999"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when restaurant does not exist")
    void shouldReturnBadRequestWhenRestaurantNotFound() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(999L, List.of(new OrderDishRequestDto(100L, 2)));

        when(jwtAdapter.extractUserId(anyString())).thenReturn(5L);
        doThrow(new RestaurantNotFoundException("Restaurant not found with id: 999"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class), eq(5L));

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Restaurant not found with id: 999"));
    }
}

