package com.pragma.plazoleta.infrastructure.persistence.mapper;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.infrastructure.persistence.entity.RestaurantEmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RestaurantEmployeeEntityMapper {
    RestaurantEmployee toDomain(RestaurantEmployeeEntity entity);
    RestaurantEmployeeEntity toEntity(RestaurantEmployee domain);
}