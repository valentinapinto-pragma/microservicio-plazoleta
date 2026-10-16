package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dish {
    private Long id;
    private String name;
    private Integer price;
    private String description;
    private String urlImage;
    private Category category;
    private Long idRestaurant;
    private Boolean active;
}
