package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.infrastructure.persistence.entity.RestaurantEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.RestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RestaurantAdapterRepository implements RestaurantPersistentPort {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantEntityMapper restaurantEntityMapper;

    @Override
    public Restaurant save(Restaurant restaurant) {
        RestaurantEntity entity = restaurantEntityMapper.toEntity(restaurant);
        return restaurantEntityMapper.toDomain(restaurantRepository.save(entity));
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        return restaurantRepository.findById(id)
                .map(restaurantEntityMapper::toDomain);
    }

    @Override
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurantEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Restaurant update(Restaurant restaurant) {
        RestaurantEntity entity = restaurantEntityMapper.toEntity(restaurant);
        return restaurantEntityMapper.toDomain(restaurantRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        restaurantRepository.deleteById(id);
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
