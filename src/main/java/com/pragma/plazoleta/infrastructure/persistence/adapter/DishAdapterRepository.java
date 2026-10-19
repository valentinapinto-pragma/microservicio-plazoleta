package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.infrastructure.persistence.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.DishEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DishAdapterRepository implements DishPersistentPort {

    private final DishRepository dishRepository;
    private final DishEntityMapper dishEntityMapper;

    @Override
    public Dish saveDish(Dish dish) {
        DishEntity entity = dishEntityMapper.toEntity(dish);
        DishEntity savedEntity = dishRepository.save(entity);
        return dishEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id).map(dishEntityMapper::toDomain);
    }
}
