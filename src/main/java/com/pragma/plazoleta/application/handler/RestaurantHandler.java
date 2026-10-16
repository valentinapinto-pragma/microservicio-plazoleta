package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.RestaurantRequestDto;
import com.pragma.plazoleta.application.mapper.RestaurantDtoMapper;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler {

    private final RestaurantServicePort restaurantServicePort;
    private final RestaurantDtoMapper restaurantDtoMapper;

    public void createRestaurant(RestaurantRequestDto restaurantRequestDto) {
        restaurantServicePort.createRestaurant(restaurantDtoMapper.toDomain(restaurantRequestDto));
    }
}
