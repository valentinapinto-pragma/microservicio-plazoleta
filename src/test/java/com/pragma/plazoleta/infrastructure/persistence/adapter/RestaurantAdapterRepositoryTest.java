package com.pragma.plazoleta.infrastructure.persistence.adapter;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.persistence.entity.RestaurantEntity;
import com.pragma.plazoleta.infrastructure.persistence.mapper.RestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.persistence.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantAdapterRepositoryTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantEntityMapper restaurantEntityMapper;

    @InjectMocks
    private RestaurantAdapterRepository restaurantAdapterRepository;

    private Restaurant restaurant;
    private RestaurantEntity entity;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant(1L, "Sabor Criollo", "900111222", "Calle 50 # 10", "+573001112233", "https://logo.png", 2L);
        entity = new RestaurantEntity(1L, "Sabor Criollo", "900111222", "Calle 50 # 10", "+573001112233", "https://logo.png", 2L);
    }

    @Test
    @DisplayName("Should save restaurant successfully through repository and mapper")
    void shouldSaveRestaurantSuccessfully() {
        when(restaurantEntityMapper.toEntity(restaurant)).thenReturn(entity);
        when(restaurantRepository.save(entity)).thenReturn(entity);
        when(restaurantEntityMapper.toDomain(entity)).thenReturn(restaurant);

        Restaurant result = restaurantAdapterRepository.saveRestaurant(restaurant);

        assertNotNull(result);
        assertEquals(restaurant.getName(), result.getName());
        verify(restaurantEntityMapper).toEntity(restaurant);
        verify(restaurantRepository).save(entity);
        verify(restaurantEntityMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should return true when restaurant exists by NIT")
    void shouldReturnTrueWhenRestaurantExistsByNit() {
        when(restaurantRepository.existsByNit("900111222")).thenReturn(true);

        boolean exists = restaurantAdapterRepository.existsByNit("900111222");

        assertTrue(exists);
        verify(restaurantRepository).existsByNit("900111222");
    }

    @Test
    @DisplayName("Should return false when restaurant does not exist by NIT")
    void shouldReturnFalseWhenRestaurantDoesNotExistByNit() {
        when(restaurantRepository.existsByNit("999999999")).thenReturn(false);

        boolean exists = restaurantAdapterRepository.existsByNit("999999999");

        assertFalse(exists);
        verify(restaurantRepository).existsByNit("999999999");
    }
}
