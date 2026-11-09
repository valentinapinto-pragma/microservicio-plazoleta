package com.pragma.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantEmployeeRequestDto {

    @NotNull(message = "The employee id must not be null")
    @Schema(description = "ID of the employee user to assign to the restaurant", example = "5")
    private Long employeeId;
}