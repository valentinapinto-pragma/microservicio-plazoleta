package com.pragma.plazoleta.infrastructure.config;

import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserValidationPort;
import com.pragma.plazoleta.domain.servicePort.DishServicePort;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;
import com.pragma.plazoleta.domain.useCase.DishUseCase;
import com.pragma.plazoleta.domain.useCase.RestaurantUseCase;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestaurantServicePort restaurantServicePort(RestaurantPersistentPort restaurantPersistentPort,
                                                       UserValidationPort userValidationPort) {
        return new RestaurantUseCase(restaurantPersistentPort, userValidationPort);
    }

    @Bean
    public DishServicePort dishServicePort(DishPersistentPort dishPersistentPort,
                                           RestaurantPersistentPort restaurantPersistentPort) {
        return new DishUseCase(dishPersistentPort, restaurantPersistentPort);
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