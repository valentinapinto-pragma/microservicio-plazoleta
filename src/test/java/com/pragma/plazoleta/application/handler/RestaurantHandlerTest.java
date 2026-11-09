package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.RestaurantRequestDto;
import com.pragma.plazoleta.application.dto.response.PaginationResponseDto;
import com.pragma.plazoleta.application.dto.response.RestaurantResponseDto;
import com.pragma.plazoleta.application.mapper.RestaurantDtoMapper;
import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantHandlerTest {

    @Mock
    private RestaurantServicePort restaurantServicePort;

    @Mock
    private RestaurantDtoMapper restaurantDtoMapper;

    @InjectMocks
    private RestaurantHandler restaurantHandler;

    @Test
    @DisplayName("Should successfully map DTO to domain and call service port")
    void shouldCreateRestaurantSuccessfully() {
        // Arrange
        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Restaurante La 80",
                "123456789",
                "Calle 10 # 5-20",
                "+573001234567",
                "https://imagen.com/logo.png",
                2L
        );
        Restaurant domainRestaurant = new Restaurant(
                null,
                "Restaurante La 80",
                "123456789",
                "Calle 10 # 5-20",
                "+573001234567",
                "https://imagen.com/logo.png",
                2L
        );

        when(restaurantDtoMapper.toDomain(requestDto)).thenReturn(domainRestaurant);

        // Act
        restaurantHandler.createRestaurant(requestDto);

        // Assert
        verify(restaurantDtoMapper).toDomain(requestDto);
        verify(restaurantServicePort).createRestaurant(domainRestaurant);
    }

    @Test
    @DisplayName("Should successfully get paged restaurants from service port and map to response DTOs")
    void shouldGetRestaurantsSuccessfully() {
        // Arrange
        int page = 0;
        int size = 5;
        Restaurant domainRestaurant = new Restaurant(1L, "Restaurante La 80", "123456789", "Calle 10", "+573001234567", "https://logo.png", 2L);
        RestaurantResponseDto responseDto = new RestaurantResponseDto("Restaurante La 80", "https://logo.png");
        CustomPage<Restaurant> customPage = new CustomPage<>(List.of(domainRestaurant), 0, 5, 1L, 1);

        when(restaurantServicePort.getRestaurants(page, size)).thenReturn(customPage);
        when(restaurantDtoMapper.toResponseDtoList(List.of(domainRestaurant))).thenReturn(List.of(responseDto));

        // Act
        PaginationResponseDto<RestaurantResponseDto> result = restaurantHandler.getRestaurants(page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Restaurante La 80", result.getContent().get(0).getName());
        assertEquals(0, result.getCurrentPage());
        assertEquals(5, result.getPageSize());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(restaurantServicePort).getRestaurants(page, size);
        verify(restaurantDtoMapper).toResponseDtoList(List.of(domainRestaurant));
    }
}