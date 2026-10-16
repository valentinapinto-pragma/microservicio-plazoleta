package com.pragma.plazoleta.infrastructure.controller;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.handler.DishHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

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
    public ResponseEntity<Map<String, String>> createDish(@Valid @RequestBody DishRequestDto dishRequestDto) {
        dishHandler.createDish(dishRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap("message", "Dish created successfully"));
    }
}
