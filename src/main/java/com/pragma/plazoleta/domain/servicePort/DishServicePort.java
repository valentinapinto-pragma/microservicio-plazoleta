package com.pragma.plazoleta.domain.servicePort;

import com.pragma.plazoleta.domain.model.Dish;

public interface DishServicePort {
    Dish createDish(Dish dish);
    Dish updateDish(Long id, Integer price, String description);
}
