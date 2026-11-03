package com.pragma.plazoleta.domain.repositoryPort;

import com.pragma.plazoleta.domain.comun.CrudBase;
import com.pragma.plazoleta.domain.model.Dish;

import java.util.List;

public interface DishPersistentPort extends CrudBase<Dish, Long> {

    List<Dish> getDishesByRestaurant(Long idRestaurant, Long categoryId, int page, int size);
}
