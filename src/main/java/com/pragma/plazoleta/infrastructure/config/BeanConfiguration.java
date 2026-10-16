package com.pragma.plazoleta.infrastructure.config;

import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserValidationPort;
import com.pragma.plazoleta.domain.servicePort.DishServicePort;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;
import com.pragma.plazoleta.domain.useCase.DishUseCase;
import com.pragma.plazoleta.domain.useCase.RestaurantUseCase;
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

}

