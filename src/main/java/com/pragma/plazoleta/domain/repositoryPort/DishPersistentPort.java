package com.pragma.plazoleta.domain.repositoryPort;

import com.pragma.plazoleta.domain.model.Dish;

import java.util.Optional;

public interface DishPersistentPort {
    Dish saveDish(Dish dish);
    Optional<Dish> findById(Long id);

}
