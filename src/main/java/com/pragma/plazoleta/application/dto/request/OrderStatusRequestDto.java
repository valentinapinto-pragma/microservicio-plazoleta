package com.pragma.plazoleta.application.dto.request;

import com.pragma.plazoleta.domain.model.OrderStatus;
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
@Schema(description = "Request body for updating order status")
public class OrderStatusRequestDto {

    @NotNull(message = "Order status cannot be null")
    @Schema(description = "Target status for the order", example = "EN_PREPARACION", required = true)
    private OrderStatus status;
}