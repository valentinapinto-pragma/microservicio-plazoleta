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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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

        Restaurant result = restaurantAdapterRepository.save(restaurant);

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

    @Test
    @DisplayName("Should return true when restaurant exists by id")
    void shouldReturnTrueWhenRestaurantExistsById() {
        when(restaurantRepository.existsById(1L)).thenReturn(true);

        boolean exists = restaurantAdapterRepository.existsById(1L);

        assertTrue(exists);
        verify(restaurantRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should return false when restaurant does not exist by id")
    void shouldReturnFalseWhenRestaurantDoesNotExistById() {
        when(restaurantRepository.existsById(99L)).thenReturn(false);

        boolean exists = restaurantAdapterRepository.existsById(99L);

        assertFalse(exists);
        verify(restaurantRepository).existsById(99L);
    }

    @Test
    @DisplayName("Should find restaurant by id through repository and mapper")
    void shouldFindRestaurantById() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(restaurantEntityMapper.toDomain(entity)).thenReturn(restaurant);

        Optional<Restaurant> result = restaurantAdapterRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(restaurant.getName(), result.get().getName());
        verify(restaurantRepository).findById(1L);
        verify(restaurantEntityMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should find all restaurants through repository and mapper")
    void shouldFindAllRestaurants() {
        when(restaurantRepository.findAll()).thenReturn(List.of(entity));
        when(restaurantEntityMapper.toDomain(entity)).thenReturn(restaurant);

        List<Restaurant> result = restaurantAdapterRepository.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(restaurant.getName(), result.get(0).getName());
        verify(restaurantRepository).findAll();
        verify(restaurantEntityMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should update restaurant successfully through repository and mapper")
    void shouldUpdateRestaurantSuccessfully() {
        when(restaurantEntityMapper.toEntity(restaurant)).thenReturn(entity);
        when(restaurantRepository.save(entity)).thenReturn(entity);
        when(restaurantEntityMapper.toDomain(entity)).thenReturn(restaurant);

        Restaurant result = restaurantAdapterRepository.update(restaurant);

        assertNotNull(result);
        assertEquals(restaurant.getName(), result.getName());
        verify(restaurantEntityMapper).toEntity(restaurant);
        verify(restaurantRepository).save(entity);
        verify(restaurantEntityMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should delete restaurant by id through repository")
    void shouldDeleteRestaurantById() {
        doNothing().when(restaurantRepository).deleteById(1L);

        restaurantAdapterRepository.delete(1L);

        verify(restaurantRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should return paged restaurants sorted alphabetically by name")
    void shouldReturnPagedRestaurantsSuccessfully() {
        Page<RestaurantEntity> pageResult = new PageImpl<>(List.of(entity));
        when(restaurantRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(restaurantEntityMapper.toDomain(entity)).thenReturn(restaurant);

        List<Restaurant> result = restaurantAdapterRepository.getRestaurants(0, 5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(restaurant.getName(), result.get(0).getName());
        verify(restaurantRepository).findAll(any(Pageable.class));
        verify(restaurantEntityMapper).toDomain(entity);
    }
}