package com.pragma.plazoleta.domain.repositoryPort;

import com.pragma.plazoleta.domain.comun.CrudBase;
import com.pragma.plazoleta.domain.model.Restaurant;

import java.util.List;

public interface RestaurantPersistentPort extends CrudBase<Restaurant, Long> {
    boolean existsByNit(String nit);
    boolean existsById(Long id);
    List<Restaurant> getRestaurants(int page, int size);

    default Restaurant saveRestaurant(Restaurant restaurant) {
        return save(restaurant);
    }
}