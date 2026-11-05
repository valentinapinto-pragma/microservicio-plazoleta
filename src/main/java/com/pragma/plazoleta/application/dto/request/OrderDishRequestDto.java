package com.pragma.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDishRequestDto {

    @NotNull(message = "The dish ID is mandatory")
    private Long idDish;

    @NotNull(message = "The quantity is mandatory")
    @Positive(message = "The dish quantity must be greater than 0")
    private Integer quantity;
}