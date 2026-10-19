package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidDishDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.servicePort.DishServicePort;

public class DishUseCase implements DishServicePort {

    private static final String INVALID_PRICE_MESSAGE = "The dish price must be an integer greater than 0";
    private static final String INVALID_DESCRIPTION_MESSAGE = "The dish description cannot be empty";
    private static final String INVALID_RESTAURANT_MESSAGE = "The restaurant associated with the dish does not exist";
    private static final String INVALID_CATEGORY_MESSAGE = "The dish must be assigned to a valid category";
    private static final String DISH_NOT_FOUND_MESSAGE = "Dish not found with id: ";

    private final DishPersistentPort dishPersistentPort;
    private final RestaurantPersistentPort restaurantPersistentPort;

    public DishUseCase(DishPersistentPort dishPersistentPort, RestaurantPersistentPort restaurantPersistentPort) {
        this.dishPersistentPort = dishPersistentPort;
        this.restaurantPersistentPort = restaurantPersistentPort;
    }

    @Override
    public Dish createDish(Dish dish) {
        validatePrice(dish.getPrice());
        validateCategory(dish);
        validateRestaurantExists(dish.getIdRestaurant());
        dish.setActive(Boolean.TRUE);
        return dishPersistentPort.saveDish(dish);
    }

    @Override
    public Dish updateDish(Long id, Integer price, String description) {
        validatePrice(price);
        validateDescription(description);
        Dish dish = dishPersistentPort.findById(id)
                .orElseThrow(() -> new DishNotFoundException(DISH_NOT_FOUND_MESSAGE + id));
        dish.setPrice(price);
        dish.setDescription(description);
        return dishPersistentPort.saveDish(dish);
    }

    private void validatePrice(Integer price) {
        if (price == null || price <= 0) {
            throw new InvalidDishDataException(INVALID_PRICE_MESSAGE);
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidDishDataException(INVALID_DESCRIPTION_MESSAGE);
        }
    }

    private void validateCategory(Dish dish) {
        if (dish.getCategory() == null || dish.getCategory().getId() == null) {
            throw new InvalidDishDataException(INVALID_CATEGORY_MESSAGE);
        }
    }

    private void validateRestaurantExists(Long idRestaurant) {
        if (idRestaurant == null || !restaurantPersistentPort.existsById(idRestaurant)) {
            throw new RestaurantNotFoundException(INVALID_RESTAURANT_MESSAGE);
        }
    }
}
