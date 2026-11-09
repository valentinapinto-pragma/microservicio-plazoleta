package com.pragma.plazoleta.infrastructure.controller;

import com.pragma.plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.pragma.plazoleta.application.dto.request.RestaurantRequestDto;
import com.pragma.plazoleta.application.dto.response.PaginationResponseDto;
import com.pragma.plazoleta.application.dto.response.RestaurantResponseDto;
import com.pragma.plazoleta.application.handler.RestaurantEmployeeHandler;
import com.pragma.plazoleta.application.handler.RestaurantHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantHandler restaurantHandler;
    private final RestaurantEmployeeHandler restaurantEmployeeHandler;

    @Operation(summary = "Create a new restaurant", description = "Allows the administrator to create a new restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data or owner not valid", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createRestaurant(@Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {
        restaurantHandler.createRestaurant(restaurantRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "List restaurants", description = "Allows the client to list all restaurants alphabetically and paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurants listed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content)
    })
    @GetMapping
    public ResponseEntity<PaginationResponseDto<RestaurantResponseDto>> getRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(restaurantHandler.getRestaurants(page, size));
    }

    @Operation(summary = "Assign an employee to a restaurant", description = "Allows the owner of the restaurant to assign an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee assigned successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data, employee already assigned, or invalid employee", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized or not the restaurant owner", content = @Content),
            @ApiResponse(responseCode = "404", description = "Restaurant not found", content = @Content)
    })
    @PostMapping("/{restaurantId}/employees")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> assignEmployeeToRestaurant(
            @PathVariable Long restaurantId,
            @Valid @RequestBody RestaurantEmployeeRequestDto requestDto) {
        restaurantEmployeeHandler.assignEmployeeToRestaurant(restaurantId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}