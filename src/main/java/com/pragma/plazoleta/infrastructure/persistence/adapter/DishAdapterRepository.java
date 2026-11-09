package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.infrastructure.persistence.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.DishEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DishAdapterRepository implements DishPersistentPort {

    private final DishRepository dishRepository;
    private final DishEntityMapper dishEntityMapper;

    @Override
    public CustomPage<Dish> getDishesByRestaurant(Long idRestaurant, Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DishEntity> dishEntities;

        if (categoryId != null) {
            dishEntities = dishRepository.findByIdRestaurantAndCategoryIdAndActiveTrue(idRestaurant, categoryId, pageable);
        } else {
            dishEntities = dishRepository.findByIdRestaurantAndActiveTrue(idRestaurant, pageable);
        }

        List<Dish> dishes = dishEntities.getContent()
                .stream()
                .map(dishEntityMapper::toDomain)
                .collect(Collectors.toList());

        return new CustomPage<>(
                dishes,
                dishEntities.getNumber(),
                dishEntities.getSize(),
                dishEntities.getTotalElements(),
                dishEntities.getTotalPages()
        );
    }

    @Override
    public Dish save(Dish dish) {
        DishEntity entity = dishEntityMapper.toEntity(dish);
        DishEntity savedEntity = dishRepository.save(entity);
        return dishEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id).map(dishEntityMapper::toDomain);
    }

    @Override
    public List<Dish> findAll() {
        return dishRepository.findAll()
                .stream()
                .map(dishEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        dishRepository.deleteById(id);
    }
}