package com.pragma.plazoleta.domain.repositoryPort;

import com.pragma.plazoleta.domain.model.Dish;

public interface DishPersistentPort {
    Dish saveDish(Dish dish);
}
