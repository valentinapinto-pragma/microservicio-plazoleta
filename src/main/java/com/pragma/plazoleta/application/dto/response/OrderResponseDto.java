package com.pragma.plazoleta.application.dto.response;

import com.pragma.plazoleta.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long id;
    private Long idClient;
    private LocalDateTime date;
    private OrderStatus status;
    private Long idChef;
    private Long idRestaurant;
    private List<OrderDishResponseDto> dishes;
}