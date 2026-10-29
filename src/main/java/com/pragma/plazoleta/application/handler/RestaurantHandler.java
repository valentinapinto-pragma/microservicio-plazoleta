package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.RestaurantRequestDto;
import com.pragma.plazoleta.application.dto.response.RestaurantResponseDto;
import com.pragma.plazoleta.application.mapper.RestaurantDtoMapper;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler {

    private final RestaurantServicePort restaurantServicePort;
    private final RestaurantDtoMapper restaurantDtoMapper;

    public void createRestaurant(RestaurantRequestDto restaurantRequestDto) {
        restaurantServicePort.createRestaurant(restaurantDtoMapper.toDomain(restaurantRequestDto));
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurants(int page, int size) {
        List<Restaurant> restaurants = restaurantServicePort.getRestaurants(page, size);
        return restaurantDtoMapper.toResponseDtoList(restaurants);
    }
}