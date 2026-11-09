package com.pragma.plazoleta.domain.repositoryPort;

import com.pragma.plazoleta.domain.comun.CrudBase;
import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface RestaurantPersistentPort extends CrudBase<Restaurant, Long> {
    boolean existsByNit(String nit);
    boolean existsById(Long id);
    CustomPage<Restaurant> getRestaurants(int page, int size);
}