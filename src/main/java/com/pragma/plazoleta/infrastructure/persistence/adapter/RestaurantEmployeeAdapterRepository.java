package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantEmployeePersistentPort;
import com.pragma.plazoleta.infrastructure.persistence.entity.RestaurantEmployeeEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.RestaurantEmployeeEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.repository.RestaurantEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RestaurantEmployeeAdapterRepository implements RestaurantEmployeePersistentPort {

    private final RestaurantEmployeeRepository restaurantEmployeeRepository;
    private final RestaurantEmployeeEntityMapper restaurantEmployeeEntityMapper;

    @Override
    public Optional<RestaurantEmployee> findByEmployeeId(Long employeeId) {
        return restaurantEmployeeRepository.findByEmployeeId(employeeId)
                .map(restaurantEmployeeEntityMapper::toDomain);
    }

    @Override
    public RestaurantEmployee save(RestaurantEmployee restaurantEmployee) {
        RestaurantEmployeeEntity entity = restaurantEmployeeEntityMapper.toEntity(restaurantEmployee);
        RestaurantEmployeeEntity savedEntity = restaurantEmployeeRepository.save(entity);
        return restaurantEmployeeEntityMapper.toDomain(savedEntity);
    }
}