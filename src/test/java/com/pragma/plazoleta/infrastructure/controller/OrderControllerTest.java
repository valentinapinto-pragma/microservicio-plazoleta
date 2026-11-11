package com.pragma.plazoleta.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.OrderDishRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderDishResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.dto.response.PaginationResponseDto;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    // TESTS: POST /orders (HU-11)

    @Test
    @DisplayName("Should return 201 Created when order is valid and user is client")
    void shouldReturnCreatedWhenValidOrder() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        doNothing().when(orderHandler).createOrder(any(OrderRequestDto.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(orderHandler).createOrder(any(OrderRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    @DisplayName("Should return 403 Forbidden when user is not a client")
    void shouldReturnForbiddenWhenUserIsNotClient() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verify(orderHandler, never()).createOrder(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when request body is invalid")
    void shouldReturnBadRequestWhenBodyInvalid() throws Exception {
        OrderRequestDto invalidDto = new OrderRequestDto(null, null);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when client already has an active order")
    void shouldReturnBadRequestWhenClientHasActiveOrder() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        doThrow(new ClientHasActiveOrderException("The client already has an active order in progress"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The client already has an active order in progress"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when dish does not belong to restaurant")
    void shouldReturnBadRequestWhenDishDoesNotBelongToRestaurant() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        doThrow(new DishDoesNotBelongToRestaurantException("The dish with id 100 does not belong to restaurant with id 1"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The dish with id 100 does not belong to restaurant with id 1"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when dish is inactive")
    void shouldReturnBadRequestWhenDishIsInactive() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(100L, 2)));

        doThrow(new DishInactiveException("The dish 'Hamburguesa' is currently inactive"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The dish 'Hamburguesa' is currently inactive"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when order contains duplicate dishes")
    void shouldReturnBadRequestWhenDuplicateDishes() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(
                new OrderDishRequestDto(100L, 1),
                new OrderDishRequestDto(100L, 2)
        ));

        doThrow(new InvalidOrderDataException("The order cannot contain duplicate dishes; please specify the total quantity"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The order cannot contain duplicate dishes; please specify the total quantity"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when restaurant does not exist on create order")
    void shouldReturnBadRequestWhenRestaurantDoesNotExist() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(999L, List.of(new OrderDishRequestDto(100L, 2)));

        doThrow(new RestaurantNotFoundException("Restaurant not found with id: 999"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Restaurant not found with id: 999"));
    }

    @Test
    @DisplayName("Should return 404 Not Found when dish does not exist on create order")
    void shouldReturnNotFoundWhenDishDoesNotExist() throws Exception {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of(new OrderDishRequestDto(999L, 2)));

        doThrow(new DishNotFoundException("Dish not found with id: 999"))
                .when(orderHandler).createOrder(any(OrderRequestDto.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Dish not found with id: 999"));
    }

    // TESTS: GET /orders (HU-12)
    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 200 OK when employee requests orders by status successfully")
    void shouldReturnOkWhenGetOrdersByStatus() throws Exception {
        OrderDishResponseDto dishDto = new OrderDishResponseDto(
                100L, "Hamburguesa", 25000, "Rica hamburguesa", "https://img.com/1", "Comida Rápida", 2
        );
        OrderResponseDto responseDto = new OrderResponseDto(
                1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 1L, List.of(dishDto)
        );
        PaginationResponseDto<OrderResponseDto> paginationResponse = new PaginationResponseDto<>(
                List.of(responseDto), 0, 10, 1L, 1
        );

        when(orderHandler.getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10))
                .thenReturn(paginationResponse);

        mockMvc.perform(get("/orders")
                        .param("status", "PENDIENTE")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].status").value("PENDIENTE"))
                .andExpect(jsonPath("$.content[0].dishes[0].name").value("Hamburguesa"))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(orderHandler, times(1)).getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 Forbidden when client tries to get orders by status")
    void shouldReturnForbiddenWhenClientGetsOrdersByStatus() throws Exception {
        mockMvc.perform(get("/orders")
                        .param("status", "PENDIENTE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(orderHandler, never()).getOrdersByStatus(any(), anyInt(), anyInt());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 400 Bad Request when employee is not assigned to any restaurant on get orders")
    void shouldReturnBadRequestWhenEmployeeNotAssignedToRestaurant() throws Exception {
        doThrow(new EmployeeNotAssignedToRestaurantException("The employee is not assigned to any restaurant"))
                .when(orderHandler).getOrdersByStatus(OrderStatus.PENDIENTE, 0, 10);

        mockMvc.perform(get("/orders")
                        .param("status", "PENDIENTE")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The employee is not assigned to any restaurant"));
    }

    // TESTS: PATCH /orders/{id}/status (HU-13)

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 200 OK when employee assigns order to themself")
    void shouldReturnOkWhenAssignOrder() throws Exception {
        Long orderId = 100L;

        OrderResponseDto responseDto = new OrderResponseDto(
                orderId, 10L, LocalDateTime.now(), OrderStatus.EN_PREPARACION, 3L, 1L, List.of()
        );

        when(orderHandler.assignOrder(orderId)).thenReturn(responseDto);

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("EN_PREPARACION"))
                .andExpect(jsonPath("$.idChef").value(3L));

        verify(orderHandler, times(1)).assignOrder(orderId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 Forbidden when client tries to assign order")
    void shouldReturnForbiddenWhenClientAssignsOrder() throws Exception {
        mockMvc.perform(patch("/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(orderHandler, never()).assignOrder(anyLong());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 404 Not Found when order does not exist on assign")
    void shouldReturnNotFoundWhenOrderNotFoundOnAssign() throws Exception {
        Long orderId = 999L;

        doThrow(new OrderNotFoundException("Order not found with id: 999"))
                .when(orderHandler).assignOrder(orderId);

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 400 Bad Request when order belongs to a different restaurant on assign")
    void shouldReturnBadRequestWhenOrderBelongsToOtherRestaurantOnAssign() throws Exception {
        Long orderId = 100L;

        doThrow(new OrderDoesNotBelongToRestaurantException("The order does not belong to the restaurant where the employee works"))
                .when(orderHandler).assignOrder(orderId);

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The order does not belong to the restaurant where the employee works"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 400 Bad Request when order status transition is invalid on assign")
    void shouldReturnBadRequestWhenInvalidOrderStatusOnAssign() throws Exception {
        Long orderId = 100L;

        doThrow(new InvalidOrderStatusException("Only orders in PENDIENTE status can be assigned to a chef"))
                .when(orderHandler).assignOrder(orderId);

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only orders in PENDIENTE status can be assigned to a chef"));
    }

    // TESTS: PATCH /orders/{id}/ready (HU-14)
    @Test
    @WithMockUser(roles = "EMPLOYEE")
    @DisplayName("Should return 200 OK when employee updates order to ready via PATCH /orders/{id}/ready")
    void shouldReturnOkWhenUpdateOrderToReady() throws Exception {
        Long orderId = 1L;
        OrderResponseDto responseDto = new OrderResponseDto(
                orderId, 10L, LocalDateTime.now(), OrderStatus.LISTO, 5L, 1L, List.of()
        );

        when(orderHandler.updateOrderToReady(orderId)).thenReturn(responseDto);

        mockMvc.perform(patch("/orders/{id}/ready", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("LISTO"));

        verify(orderHandler, times(1)).updateOrderToReady(orderId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 Forbidden when client tries to mark order as ready")
    void shouldReturnForbiddenWhenClientMarksOrderReady() throws Exception {
        mockMvc.perform(patch("/orders/1/ready")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(orderHandler, never()).updateOrderToReady(anyLong());
    }
}