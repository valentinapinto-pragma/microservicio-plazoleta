package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.infrastructure.persistence.entity.RestaurantEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.RestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantAdapterRepository implements RestaurantPersistentPort {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantEntityMapper restaurantEntityMapper;

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        RestaurantEntity entity = restaurantEntityMapper.toEntity(restaurant);
        RestaurantEntity savedEntity = restaurantRepository.save(entity);
        return restaurantEntityMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByNit(String nit) {
        return restaurantRepository.existsByNit(nit);
    }

    @Override
    public boolean existsById(Long id) {
        return restaurantRepository.existsById(id);
    }
}
