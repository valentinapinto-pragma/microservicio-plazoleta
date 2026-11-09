package com.pragma.plazoleta.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.OrderDishRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderStatusRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderDishResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.handler.OrderHandler;
import com.pragma.plazoleta.domain.exception.*;
import com.pragma.plazoleta.domain.model.OrderStatus;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    // TESTS: POST /orders (HU-11)

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

    // TESTS: GET /orders (HU-12)
    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 200 OK with orders list when request is valid and user is employee")
    void shouldReturnOkWhenGetOrdersByStatusValid() throws Exception {
        OrderDishResponseDto dishDto = new OrderDishResponseDto(100L, "Hamburguesa", 25000, "Rica", "http://img.com/1", "Comida", 2);
        OrderResponseDto orderDto = new OrderResponseDto(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 2L, List.of(dishDto));

        when(jwtAdapter.extractUserId(anyString())).thenReturn(50L);
        when(orderHandler.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10, 50L)).thenReturn(List.of(orderDto));

        mockMvc.perform(get("/orders")
                        .param("status", "PENDIENTE")
                        .param("page", "0")
                        .param("size", "10")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].idClient").value(10L))
                .andExpect(jsonPath("$[0].status").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].idRestaurant").value(2L))
                .andExpect(jsonPath("$[0].dishes[0].name").value("Hamburguesa"))
                .andExpect(jsonPath("$[0].dishes[0].quantity").value(2));

        verify(jwtAdapter).extractUserId(AUTH_HEADER);
        verify(orderHandler).getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10, 50L);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 Forbidden when user is not an employee on GET /orders")
    void shouldReturnForbiddenWhenUserIsNotEmployeeOnGetOrders() throws Exception {
        mockMvc.perform(get("/orders")
                        .param("status", "PENDIENTE")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isForbidden());

        verify(orderHandler, never()).getOrdersByStatus(any(), anyInt(), anyInt(), anyLong());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 400 Bad Request when employee is not assigned to any restaurant")
    void shouldReturnBadRequestWhenEmployeeNotAssignedToRestaurant() throws Exception {
        when(jwtAdapter.extractUserId(anyString())).thenReturn(50L);
        doThrow(new EmployeeNotAssignedToRestaurantException("The employee is not assigned to any restaurant"))
                .when(orderHandler).getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10, 50L);

        mockMvc.perform(get("/orders")
                        .param("status", "PENDIENTE")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The employee is not assigned to any restaurant"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 400 Bad Request when pagination parameters are invalid")
    void shouldReturnBadRequestWhenPaginationInvalid() throws Exception {
        when(jwtAdapter.extractUserId(anyString())).thenReturn(50L);
        doThrow(new InvalidOrderDataException("The page must be greater than or equal to 0"))
                .when(orderHandler).getOrdersByStatus(OrderStatus.PENDIENTE, -1, 10, 50L);

        mockMvc.perform(get("/orders")
                        .param("status", "PENDIENTE")
                        .param("page", "-1")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The page must be greater than or equal to 0"));
    }

    // TESTS: PATCH /orders/{id}/status (HU-13)

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 200 OK with updated order when assign order is valid and user is employee")
    void shouldAssignOrderSuccessfullyWhenUserIsEmployee() throws Exception {
        Long orderId = 100L;
        Long employeeId = 3L;
        OrderStatusRequestDto requestDto = new OrderStatusRequestDto(OrderStatus.EN_PREPARACION);

        OrderDishResponseDto dishDto = new OrderDishResponseDto(100L, "Hamburguesa", 25000, "Rica", "http://img.com/1", "Comida", 2);
        OrderResponseDto responseDto = new OrderResponseDto(orderId, 10L, LocalDateTime.now(), OrderStatus.EN_PREPARACION, employeeId, 1L, List.of(dishDto));

        when(jwtAdapter.extractUserId(anyString())).thenReturn(employeeId);
        when(orderHandler.assignOrder(eq(orderId), any(OrderStatusRequestDto.class), eq(employeeId))).thenReturn(responseDto);

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("EN_PREPARACION"))
                .andExpect(jsonPath("$.idChef").value(employeeId))
                .andExpect(jsonPath("$.dishes[0].name").value("Hamburguesa"));

        verify(jwtAdapter).extractUserId(AUTH_HEADER);
        verify(orderHandler).assignOrder(eq(orderId), any(OrderStatusRequestDto.class), eq(employeeId));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 Forbidden when user is not an employee on PATCH /orders/{id}/status")
    void shouldReturnForbiddenWhenUserIsNotEmployeeOnAssignOrder() throws Exception {
        OrderStatusRequestDto requestDto = new OrderStatusRequestDto(OrderStatus.EN_PREPARACION);

        mockMvc.perform(patch("/orders/{id}/status", 100L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());

        verify(orderHandler, never()).assignOrder(anyLong(), any(), anyLong());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 404 Not Found when order does not exist on assign")
    void shouldReturnNotFoundWhenOrderNotFoundOnAssign() throws Exception {
        Long orderId = 999L;
        Long employeeId = 3L;
        OrderStatusRequestDto requestDto = new OrderStatusRequestDto(OrderStatus.EN_PREPARACION);

        when(jwtAdapter.extractUserId(anyString())).thenReturn(employeeId);
        doThrow(new OrderNotFoundException("Order not found with id: 999"))
                .when(orderHandler).assignOrder(eq(orderId), any(OrderStatusRequestDto.class), eq(employeeId));

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 400 Bad Request when order does not belong to employee's restaurant")
    void shouldReturnBadRequestWhenOrderDoesNotBelongToRestaurantOnAssign() throws Exception {
        Long orderId = 100L;
        Long employeeId = 3L;
        OrderStatusRequestDto requestDto = new OrderStatusRequestDto(OrderStatus.EN_PREPARACION);

        when(jwtAdapter.extractUserId(anyString())).thenReturn(employeeId);
        doThrow(new OrderDoesNotBelongToRestaurantException("The order does not belong to the restaurant where the employee works"))
                .when(orderHandler).assignOrder(eq(orderId), any(OrderStatusRequestDto.class), eq(employeeId));

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The order does not belong to the restaurant where the employee works"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 400 Bad Request when order status transition is invalid on assign")
    void shouldReturnBadRequestWhenInvalidOrderStatusOnAssign() throws Exception {
        Long orderId = 100L;
        Long employeeId = 3L;
        OrderStatusRequestDto requestDto = new OrderStatusRequestDto(OrderStatus.EN_PREPARACION);

        when(jwtAdapter.extractUserId(anyString())).thenReturn(employeeId);
        doThrow(new InvalidOrderStatusException("Only orders in PENDIENTE status can be assigned to a chef"))
                .when(orderHandler).assignOrder(eq(orderId), any(OrderStatusRequestDto.class), eq(employeeId));

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only orders in PENDIENTE status can be assigned to a chef"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 400 Bad Request when status is null in body on assign")
    void shouldReturnBadRequestWhenStatusIsNullInBodyOnAssign() throws Exception {
        mockMvc.perform(patch("/orders/{id}/status", 100L)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("status: Order status cannot be null"));

        verify(orderHandler, never()).assignOrder(anyLong(), any(), anyLong());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 200 OK when employee updates order to ready via PATCH /orders/{id}/ready")
    void shouldReturnOkWhenUpdateOrderToReady() throws Exception {
        Long orderId = 1L;
        Long employeeId = 5L;

        OrderResponseDto responseDto = new OrderResponseDto(
                orderId, 10L, LocalDateTime.now(), OrderStatus.LISTO, employeeId, 1L, List.of(), "123456"
        );

        when(jwtAdapter.extractUserId(AUTH_HEADER)).thenReturn(employeeId);
        when(orderHandler.updateOrderToReady(orderId, employeeId)).thenReturn(responseDto);

        mockMvc.perform(patch("/orders/{id}/ready", orderId)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("LISTO"))
                .andExpect(jsonPath("$.securityPin").value("123456"));

        verify(orderHandler, times(1)).updateOrderToReady(orderId, employeeId);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 200 OK when employee updates order to ready via PATCH /orders/{id}/status with LISTO")
    void shouldReturnOkWhenUpdateOrderStatusToListo() throws Exception {
        Long orderId = 1L;
        Long employeeId = 5L;
        OrderStatusRequestDto requestDto = new OrderStatusRequestDto(OrderStatus.LISTO);

        OrderResponseDto responseDto = new OrderResponseDto(
                orderId, 10L, LocalDateTime.now(), OrderStatus.LISTO, employeeId, 1L, List.of(), "654321"
        );

        when(jwtAdapter.extractUserId(AUTH_HEADER)).thenReturn(employeeId);
        when(orderHandler.updateOrderToReady(orderId, employeeId)).thenReturn(responseDto);

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("LISTO"))
                .andExpect(jsonPath("$.securityPin").value("654321"));

        verify(orderHandler, times(1)).updateOrderToReady(orderId, employeeId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 Forbidden when client tries to mark order as ready")
    void shouldReturnForbiddenWhenClientMarksOrderReady() throws Exception {
        mockMvc.perform(patch("/orders/1/ready")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                .andExpect(status().isForbidden());

        verify(orderHandler, never()).updateOrderToReady(anyLong(), anyLong());
    }
}