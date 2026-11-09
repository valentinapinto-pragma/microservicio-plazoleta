package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.pragma.plazoleta.domain.servicePort.RestaurantEmployeeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantEmployeeHandler {

    private final RestaurantEmployeeServicePort restaurantEmployeeServicePort;

    public void assignEmployeeToRestaurant(Long restaurantId, RestaurantEmployeeRequestDto requestDto, Long authenticatedOwnerId) {
        restaurantEmployeeServicePort.assignEmployeeToRestaurant(
                restaurantId,
                requestDto.getEmployeeId(),
                authenticatedOwnerId
        );
    }
}