package com.pragma.plazoleta.infrastructure.config;

import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserValidationPort;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;
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
    private UserValidationPort userValidationPort;

    @Test
    @DisplayName("Should create RestaurantServicePort bean instance")
    void shouldCreateRestaurantServicePortBean() {
        BeanConfiguration config = new BeanConfiguration();
        RestaurantServicePort bean = config.restaurantServicePort(restaurantPersistentPort, userValidationPort);
        assertNotNull(bean);
    }
}
