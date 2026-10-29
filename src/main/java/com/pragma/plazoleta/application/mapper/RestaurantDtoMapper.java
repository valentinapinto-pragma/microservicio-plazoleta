package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.RestaurantRequestDto;
import com.pragma.plazoleta.application.dto.response.RestaurantResponseDto;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RestaurantDtoMapper {

    Restaurant toDomain(RestaurantRequestDto restaurantRequestDto);

    RestaurantResponseDto toResponseDto(Restaurant restaurant);

    List<RestaurantResponseDto> toResponseDtoList(List<Restaurant> restaurants);
}