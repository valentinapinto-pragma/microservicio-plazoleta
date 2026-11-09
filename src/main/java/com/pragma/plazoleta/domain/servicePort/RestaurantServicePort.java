package com.pragma.plazoleta.domain.servicePort;

import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface RestaurantServicePort {
    Restaurant createRestaurant(Restaurant restaurant);
    CustomPage<Restaurant> getRestaurants(int page, int size);
}