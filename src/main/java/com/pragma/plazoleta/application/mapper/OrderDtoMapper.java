package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.OrderDishRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderDishResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrderDtoMapper {

    @Mapping(source = "idRestaurant", target = "restaurantId")
    @Mapping(source = "dishes", target = "orderDishes")
    Order toDomain(OrderRequestDto orderRequestDto);

    @Mapping(source = "idDish", target = "dishId")
    OrderDish toDomain(OrderDishRequestDto orderDishRequestDto);

    @Mapping(source = "clientId", target = "idClient")
    @Mapping(source = "restaurantId", target = "idRestaurant")
    @Mapping(source = "chefId", target = "idChef")
    @Mapping(source = "orderDishes", target = "dishes")
    OrderResponseDto toResponseDto(Order order);

    @Mapping(source = "dishId", target = "idDish")
    @Mapping(source = "dish.name", target = "name")
    @Mapping(source = "dish.price", target = "price")
    @Mapping(source = "dish.description", target = "description")
    @Mapping(source = "dish.urlImage", target = "urlImage")
    @Mapping(source = "dish.category.name", target = "category")
    @Mapping(source = "quantity", target = "quantity")
    OrderDishResponseDto toResponseDto(OrderDish orderDish);

    List<OrderResponseDto> toResponseDtoList(List<Order> orders);
}