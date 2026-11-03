package com.pragma.plazoleta.domain.servicePort;

import com.pragma.plazoleta.domain.model.Dish;

import java.util.List;

public interface DishServicePort {
    Dish createDish(Dish dish);
    Dish updateDish(Long id, Integer price, String description);
    Dish changeDishStatus(Long id, Boolean active);
    List<Dish> getDishesByRestaurant(Long idRestaurant, Long categoryId, int page, int size);
}