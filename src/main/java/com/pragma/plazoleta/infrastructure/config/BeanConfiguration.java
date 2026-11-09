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
import com.pragma.plazoleta.domain.servicePort.RestaurantEmployeeServicePort;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;
import com.pragma.plazoleta.domain.useCase.DishUseCase;
import com.pragma.plazoleta.domain.useCase.OrderUseCase;
import com.pragma.plazoleta.domain.useCase.RestaurantEmployeeUseCase;
import com.pragma.plazoleta.domain.useCase.RestaurantUseCase;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestaurantServicePort restaurantServicePort(RestaurantPersistentPort restaurantPersistentPort,
                                                       UserInformationPort userInformationPort) {
        return new RestaurantUseCase(restaurantPersistentPort, userInformationPort);
    }

    @Bean
    public RestaurantEmployeeServicePort restaurantEmployeeServicePort(
            RestaurantPersistentPort restaurantPersistentPort,
            RestaurantEmployeePersistentPort restaurantEmployeePersistentPort,
            UserInformationPort userInformationPort) {
        return new RestaurantEmployeeUseCase(restaurantPersistentPort, restaurantEmployeePersistentPort, userInformationPort);
    }

    @Bean
    public DishServicePort dishServicePort(DishPersistentPort dishPersistentPort,
                                           RestaurantPersistentPort restaurantPersistentPort,
                                           UserInformationPort userInformationPort) {
        return new DishUseCase(dishPersistentPort, restaurantPersistentPort, userInformationPort);
    }

    @Bean
    public DishHandler dishHandler(DishServicePort dishServicePort, DishDtoMapper dishDtoMapper) {
        return new DishHandler(dishServicePort, dishDtoMapper);
    }

    @Bean
    public OrderServicePort orderServicePort(OrderPersistentPort orderPersistentPort,
                                             DishPersistentPort dishPersistentPort,
                                             RestaurantPersistentPort restaurantPersistentPort,
                                             RestaurantEmployeePersistentPort restaurantEmployeePersistentPort,
                                             UserInformationPort userInformationPort,
                                             MessageNotificationPort messageNotificationPort) {
        return new OrderUseCase(orderPersistentPort, dishPersistentPort, restaurantPersistentPort, restaurantEmployeePersistentPort, userInformationPort, messageNotificationPort);
    }

    @Bean
    public OrderHandler orderHandler(OrderServicePort orderServicePort, OrderDtoMapper orderDtoMapper) {
        return new OrderHandler(orderServicePort, orderDtoMapper);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Plazoleta - Plazoleta de Comidas")
                        .version("1.0.0")
                        .description("API REST para la gestion de restaurantes y platos.")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}