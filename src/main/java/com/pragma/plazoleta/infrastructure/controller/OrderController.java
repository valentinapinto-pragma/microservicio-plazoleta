package com.pragma.plazoleta.infrastructure.controller;

import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.handler.OrderHandler;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
}

