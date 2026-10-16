package com.pragma.plazoleta.application.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantRequestDto {

    @NotBlank(message = "The name is mandatory")
    @Pattern(regexp = "^(?!^\\d+$).+$", message = "The restaurant name cannot consist solely of numbers")
    private String name;

    @NotBlank(message = "The NIT is mandatory")
    @Pattern(regexp = "^\\d+$", message = "The NIT must be strictly numeric")
    private String nit;

    @NotBlank(message = "The address is mandatory")
    private String address;

    @NotBlank(message = "The phone is mandatory")
    @Pattern(regexp = "^\\+?\\d{1,13}$", message = "The phone number must be numeric, max 13 characters, and may start with '+'")
    private String phone;

    @NotBlank(message = "The logo URL is mandatory")
    private String urlLogo;

    @NotNull(message = "The owner ID is mandatory")
    private Long idOwner;
}
