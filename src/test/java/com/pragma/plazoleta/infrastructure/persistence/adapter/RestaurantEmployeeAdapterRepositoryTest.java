package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.infrastructure.persistence.entity.RestaurantEmployeeEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.RestaurantEmployeeEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.repository.RestaurantEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantEmployeeAdapterRepositoryTest {

    @Mock
    private RestaurantEmployeeRepository restaurantEmployeeRepository;

    @Mock
    private RestaurantEmployeeEntityMapper restaurantEmployeeEntityMapper;

    @InjectMocks
    private RestaurantEmployeeAdapterRepository restaurantEmployeeAdapterRepository;

    private RestaurantEmployee domain;
    private RestaurantEmployeeEntity entity;

    @BeforeEach
    void setUp() {
        domain = new RestaurantEmployee(1L, 10L, 20L);
        entity = new RestaurantEmployeeEntity(1L, 10L, 20L);
    }

    @Test
    @DisplayName("Should return employee when found by employee id")
    void shouldReturnEmployeeWhenFoundByEmployeeId() {
        // Arrange
        when(restaurantEmployeeRepository.findByEmployeeId(20L)).thenReturn(Optional.of(entity));
        when(restaurantEmployeeEntityMapper.toDomain(entity)).thenReturn(domain);

        // Act
        Optional<RestaurantEmployee> result = restaurantEmployeeAdapterRepository.findByEmployeeId(20L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(domain.getEmployeeId(), result.get().getEmployeeId());
        verify(restaurantEmployeeRepository, times(1)).findByEmployeeId(20L);
        verify(restaurantEmployeeEntityMapper, times(1)).toDomain(entity);
    }

    @Test
    @DisplayName("Should return empty optional when employee not found by employee id")
    void shouldReturnEmptyWhenNotFoundByEmployeeId() {
        // Arrange
        when(restaurantEmployeeRepository.findByEmployeeId(99L)).thenReturn(Optional.empty());

        // Act
        Optional<RestaurantEmployee> result = restaurantEmployeeAdapterRepository.findByEmployeeId(99L);

        // Assert
        assertTrue(result.isEmpty());
        verify(restaurantEmployeeRepository, times(1)).findByEmployeeId(99L);
        verify(restaurantEmployeeEntityMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should save restaurant employee through repository and mapper")
    void shouldSaveRestaurantEmployee() {
        // Arrange
        when(restaurantEmployeeEntityMapper.toEntity(domain)).thenReturn(entity);
        when(restaurantEmployeeRepository.save(entity)).thenReturn(entity);
        when(restaurantEmployeeEntityMapper.toDomain(entity)).thenReturn(domain);

        // Act
        RestaurantEmployee result = restaurantEmployeeAdapterRepository.save(domain);

        // Assert
        assertNotNull(result);
        assertEquals(domain.getId(), result.getId());
        verify(restaurantEmployeeEntityMapper, times(1)).toEntity(domain);
        verify(restaurantEmployeeRepository, times(1)).save(entity);
        verify(restaurantEmployeeEntityMapper, times(1)).toDomain(entity);
    }
}