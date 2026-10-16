package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.RestaurantRequestDto;
import com.pragma.plazoleta.application.mapper.RestaurantDtoMapper;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
