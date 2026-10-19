package com.pragma.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDishRequestDto {

    @NotNull(message = "The price is mandatory")
    @Positive(message = "The price must be an integer greater than 0")
    private Integer price;

    @NotBlank(message = "The description is mandatory")
    private String description;
}
