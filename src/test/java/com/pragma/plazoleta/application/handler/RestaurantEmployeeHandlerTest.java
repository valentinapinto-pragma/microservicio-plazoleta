package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.RestaurantEmployeeRequestDto;
import com.pragma.plazoleta.domain.servicePort.RestaurantEmployeeServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestaurantEmployeeHandlerTest {

    @Mock
    private RestaurantEmployeeServicePort restaurantEmployeeServicePort;

    @InjectMocks
    private RestaurantEmployeeHandler restaurantEmployeeHandler;

    @Test
    @DisplayName("Should delegate employee assignment to domain service port")
    void shouldDelegateToDomainServicePort() {
        Long restaurantId = 1L;
        Long employeeId = 5L;
        RestaurantEmployeeRequestDto requestDto = new RestaurantEmployeeRequestDto(employeeId);

        restaurantEmployeeHandler.assignEmployeeToRestaurant(restaurantId, requestDto);

        verify(restaurantEmployeeServicePort, times(1))
                .assignEmployeeToRestaurant(restaurantId, employeeId);
    }
}