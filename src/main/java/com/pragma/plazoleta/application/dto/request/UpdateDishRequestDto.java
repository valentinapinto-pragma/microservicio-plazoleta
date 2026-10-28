package com.pragma.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDishRequestDto {

    @Positive(message = "The price must be an integer greater than 0")
    private Integer price;

    @Pattern(regexp = "^(?!\\s*$).+", message = "The dish description cannot be empty")
    private String description;
}