package com.pragma.plazoleta.domain.servicePort;

import com.pragma.plazoleta.domain.model.Restaurant;

import java.util.List;

public interface RestaurantServicePort {
    Restaurant createRestaurant(Restaurant restaurant);
    List<Restaurant> getRestaurants(int page, int size);
}