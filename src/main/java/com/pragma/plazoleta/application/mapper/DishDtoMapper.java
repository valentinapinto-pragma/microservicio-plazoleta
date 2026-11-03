package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DishDtoMapper {

    @Mapping(target = "category.id", source = "idCategory")
    Dish toDomain(DishRequestDto dishRequestDto);

    @Mapping(target = "category", source = "category.name")
    DishResponseDto toResponseDto(Dish dish);

    List<DishResponseDto> toResponseDtoList(List<Dish> dishes);
}