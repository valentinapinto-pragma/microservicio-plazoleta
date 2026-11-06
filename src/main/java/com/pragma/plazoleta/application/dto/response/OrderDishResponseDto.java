package com.pragma.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDishResponseDto {
    private Long idDish;
    private String name;
    private Integer price;
    private String description;
    private String urlImage;
    private String category;
    private Integer quantity;
}