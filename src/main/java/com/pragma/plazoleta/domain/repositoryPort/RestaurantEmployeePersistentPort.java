package com.pragma.plazoleta.domain.repositoryPort;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;

import java.util.Optional;

public interface RestaurantEmployeePersistentPort {
    Optional<RestaurantEmployee> findByEmployeeId(Long employeeId);
    RestaurantEmployee save(RestaurantEmployee restaurantEmployee);
}