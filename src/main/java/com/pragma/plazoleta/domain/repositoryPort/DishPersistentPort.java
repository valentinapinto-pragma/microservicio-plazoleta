package com.pragma.plazoleta.domain.repositoryPort;

import com.pragma.plazoleta.domain.comun.CrudBase;
import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Dish;

public interface DishPersistentPort extends CrudBase<Dish, Long> {
    CustomPage<Dish> getDishesByRestaurant(Long idRestaurant, Long categoryId, int page, int size);
}