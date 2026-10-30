package com.pragma.plazoleta.infrastructure.config;

import com.pragma.plazoleta.application.handler.DishHandler;
import com.pragma.plazoleta.application.mapper.DishDtoMapper;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserInformationPort;
import com.pragma.plazoleta.domain.servicePort.DishServicePort;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class BeanConfigurationTest {

    @Mock
    private RestaurantPersistentPort restaurantPersistentPort;

    @Mock
    private UserInformationPort userInformationPort;

    @Mock
    private DishPersistentPort dishPersistentPort;

    @Mock
    private DishDtoMapper dishDtoMapper;

    @Test
    @DisplayName("Should create all bean instances correctly")
    void shouldCreateAllBeanInstances() {
        BeanConfiguration config = new BeanConfiguration();

        RestaurantServicePort restaurantBean = config.restaurantServicePort(restaurantPersistentPort, userInformationPort);
        DishServicePort dishBean = config.dishServicePort(dishPersistentPort, restaurantPersistentPort, userInformationPort);
        DishHandler dishHandlerBean = config.dishHandler(dishBean, dishDtoMapper);
        OpenAPI openAPIBean = config.customOpenAPI();

        assertNotNull(restaurantBean);
        assertNotNull(dishBean);
        assertNotNull(dishHandlerBean);
        assertNotNull(openAPIBean);
    }
}