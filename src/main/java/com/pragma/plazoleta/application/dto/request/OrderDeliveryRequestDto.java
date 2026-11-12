package com.pragma.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDeliveryRequestDto {

    @NotBlank(message = "The security PIN is mandatory")
    @Pattern(regexp = "^\\d{6}$", message = "The security PIN must be exactly 6 numeric digits")
    private String securityPin;
}