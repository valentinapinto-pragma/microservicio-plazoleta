package com.pragma.plazoleta.domain.repositoryPort;

import com.pragma.plazoleta.domain.model.Restaurant;

public interface RestaurantPersistentPort {
    Restaurant saveRestaurant(Restaurant restaurant);
    boolean existsByNit(String nit);
    boolean existsById(Long id);

}
