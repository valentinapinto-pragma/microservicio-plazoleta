package com.pragma.plazoleta.infrastructure.controller;

import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderStatusRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.application.handler.OrderHandler;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.infrastructure.security.JwtAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderHandler orderHandler;
    private final JwtAdapter jwtAdapter;

    @Operation(summary = "Create a new order", description = "Allows an authenticated client to create a new order for a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid order data, active order in progress, or dish does not belong to restaurant", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized or not a client", content = @Content),
            @ApiResponse(responseCode = "404", description = "Restaurant or dish not found", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> createOrder(
            @Valid @RequestBody OrderRequestDto orderRequestDto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Long clientId = jwtAdapter.extractUserId(authHeader);
        orderHandler.createOrder(orderRequestDto, clientId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get orders filtered by status", description = "Allows an employee to list orders from their assigned restaurant filtered by status with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status, pagination parameters, or employee not assigned to any restaurant", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized or not an employee", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(
            @RequestParam OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Long employeeId = jwtAdapter.extractUserId(authHeader);
        List<OrderResponseDto> orders = orderHandler.getOrdersByStatus(status, page, size, employeeId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Assign order to employee and update status to EN_PREPARACION",
            description = "Allows an authenticated employee to assign an order to themselves and update its status to EN_PREPARACION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition, order does not belong to employee's restaurant, or employee not assigned to any restaurant", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized or not an employee", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<OrderResponseDto> assignOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequestDto orderStatusRequestDto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Long employeeId = jwtAdapter.extractUserId(authHeader);
        OrderResponseDto updatedOrder = orderHandler.assignOrder(id, orderStatusRequestDto, employeeId);
        return ResponseEntity.ok(updatedOrder);
    }
}