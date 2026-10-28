package com.pragma.plazoleta.infrastructure.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    private String message;
}
