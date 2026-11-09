package com.pragma.plazoleta.infrastructure.config;

import com.pragma.plazoleta.application.handler.DishHandler;
import com.pragma.plazoleta.application.handler.OrderHandler;
import com.pragma.plazoleta.application.mapper.DishDtoMapper;
import com.pragma.plazoleta.application.mapper.OrderDtoMapper;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.MessageNotificationPort;
import com.pragma.plazoleta.domain.repositoryPort.OrderPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantEmployeePersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserInformationPort;
import com.pragma.plazoleta.domain.servicePort.DishServicePort;
import com.pragma.plazoleta.domain.servicePort.OrderServicePort;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

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

    @Mock
    private OrderPersistentPort orderPersistentPort;

    @Mock
    private RestaurantEmployeePersistentPort restaurantEmployeePersistentPort;

    @Mock
    private MessageNotificationPort messageNotificationPort;

    @Mock
    private OrderDtoMapper orderDtoMapper;

    @Test
    @DisplayName("Should create all bean instances correctly")
    void shouldCreateAllBeanInstances() {
        BeanConfiguration config = new BeanConfiguration();

        RestTemplate restTemplateBean = config.restTemplate();
        RestaurantServicePort restaurantBean = config.restaurantServicePort(restaurantPersistentPort, userInformationPort);
        DishServicePort dishBean = config.dishServicePort(dishPersistentPort, restaurantPersistentPort, userInformationPort);
        DishHandler dishHandlerBean = config.dishHandler(dishBean, dishDtoMapper);
        OrderServicePort orderBean = config.orderServicePort(
                orderPersistentPort,
                dishPersistentPort,
                restaurantPersistentPort,
                restaurantEmployeePersistentPort,
                userInformationPort,
                messageNotificationPort
        );
        OrderHandler orderHandlerBean = config.orderHandler(orderBean, orderDtoMapper);
        OpenAPI openAPIBean = config.customOpenAPI();

        assertNotNull(restTemplateBean);
        assertNotNull(restaurantBean);
        assertNotNull(dishBean);
        assertNotNull(dishHandlerBean);
        assertNotNull(orderBean);
        assertNotNull(orderHandlerBean);
        assertNotNull(openAPIBean);
    }
}