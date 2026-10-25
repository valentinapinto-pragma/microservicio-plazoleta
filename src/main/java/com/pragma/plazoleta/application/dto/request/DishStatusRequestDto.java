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
@Schema(description = "DTO for enabling or disabling a dish")
public class DishStatusRequestDto {

    @NotNull(message = "The active status is mandatory")
    @Schema(description = "Dish status (true to enable, false to disable)", example = "false", required = true)
    private Boolean active;
}
