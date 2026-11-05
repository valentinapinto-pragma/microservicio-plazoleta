package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.OrderDishRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrderDtoMapper {

    @Mapping(source = "idRestaurant", target = "restaurantId")
    @Mapping(source = "dishes", target = "orderDishes")
    Order toDomain(OrderRequestDto orderRequestDto);

    @Mapping(source = "idDish", target = "dishId")
    OrderDish toDomain(OrderDishRequestDto orderDishRequestDto);
}