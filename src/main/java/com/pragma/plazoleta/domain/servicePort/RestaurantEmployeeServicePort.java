package com.pragma.plazoleta.domain.servicePort;

public interface RestaurantEmployeeServicePort {
    void assignEmployeeToRestaurant(Long restaurantId, Long employeeId);
}