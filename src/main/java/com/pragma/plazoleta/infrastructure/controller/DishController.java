package com.pragma.plazoleta.infrastructure.controller;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishStatusRequestDto;
import com.pragma.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.pragma.plazoleta.application.handler.DishHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishHandler dishHandler;

    @Operation(summary = "Create a new dish", description = "Allows the owner to create a new dish for their restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data or restaurant not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> createDish(@Valid @RequestBody DishRequestDto dishRequestDto) {
        dishHandler.createDish(dishRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Update a dish", description = "Allows the owner to update the price and description of an existing dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Dish not found", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateDish(@PathVariable Long id,
                                           @Valid @RequestBody UpdateDishRequestDto updateDishRequestDto) {
        dishHandler.updateDish(id, updateDishRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Enable or disable a dish", description = "Allows the owner of the restaurant to enable or disable a dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish status updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized or not the restaurant owner", content = @Content),
            @ApiResponse(responseCode = "404", description = "Dish or restaurant not found", content = @Content)
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> changeDishStatus(
            @PathVariable Long id,
            @Valid @RequestBody DishStatusRequestDto dishStatusRequestDto) {
        dishHandler.changeDishStatus(id, dishStatusRequestDto);
        return ResponseEntity.ok().build();
    }
}