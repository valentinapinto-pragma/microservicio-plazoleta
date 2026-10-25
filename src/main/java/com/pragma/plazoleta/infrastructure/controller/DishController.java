package com.pragma.plazoleta.infrastructure.controller;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishStatusRequestDto;
import com.pragma.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.pragma.plazoleta.application.handler.DishHandler;
import com.pragma.plazoleta.infrastructure.security.JwtAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishHandler dishHandler;
    private final JwtAdapter jwtAdapter;

    @Operation(summary = "Create a new dish", description = "Allows the owner to create a new dish for their restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data or restaurant not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Map<String, String>> createDish(@Valid @RequestBody DishRequestDto dishRequestDto) {
        dishHandler.createDish(dishRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap("message", "Dish created successfully"));
    }

    @Operation(summary = "Update a dish", description = "Allows the owner to update the price and description of an existing dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Dish not found", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateDish(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateDishRequestDto updateDishRequestDto) {
        dishHandler.updateDish(id, updateDishRequestDto);
        return ResponseEntity.ok(Collections.singletonMap("message", "Dish updated successfully"));
    }

    @Operation(summary = "Enable or disable a dish", description = "Allows the owner of the restaurant to enable or disable a dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish status updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized or not the restaurant owner", content = @Content),
            @ApiResponse(responseCode = "404", description = "Dish or restaurant not found", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> changeDishStatus(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @Valid @RequestBody DishStatusRequestDto dishStatusRequestDto) {

        if (token == null || !"ROLE_OWNER".equals(jwtAdapter.extractRole(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", "User must possess the OWNER role to modify dishes"));
        }

        Long ownerId = jwtAdapter.extractUserId(token);
        dishHandler.changeDishStatus(id, dishStatusRequestDto, ownerId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Dish status updated successfully"));
    }
}
