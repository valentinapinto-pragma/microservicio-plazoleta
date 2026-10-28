package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidDishDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotOwnerOfRestaurantException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.repositoryPort.DishPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.servicePort.DishServicePort;

public class DishUseCase implements DishServicePort {

    private static final String AT_LEAST_ONE_FIELD_MESSAGE = "At least one field must be provided for update";

    private static final String INVALID_PRICE_MESSAGE = "The dish price must be an integer greater than 0";
    private static final String INVALID_DESCRIPTION_MESSAGE = "The dish description cannot be empty";
    private static final String INVALID_RESTAURANT_MESSAGE = "The restaurant associated with the dish does not exist";
    private static final String INVALID_CATEGORY_MESSAGE = "The dish must be assigned to a valid category";
    private static final String INVALID_STATUS_MESSAGE = "The active status cannot be null";
    private static final String DISH_NOT_FOUND_MESSAGE = "Dish not found with id: ";
    private static final String NOT_OWNER_MESSAGE = "The user is not the owner of the restaurant to which the dish belongs";

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
        return dishPersistentPort.save(dish);
    }

    @Override
    public Dish updateDish(Long id, Integer price, String description) {
        // 1. Regla de negocio: Al menos un campo debe estar presente
        if (price == null && (description == null || description.trim().isEmpty())) {
            throw new InvalidDishDataException(AT_LEAST_ONE_FIELD_MESSAGE);
        }

        // 2. Validaciones condicionales (Fail-Fast antes de ir a BD)
        if (price != null) {
            validatePrice(price);
        }
        if (description != null) {
            validateDescription(description);
        }

        // 3. Buscar el plato existente (o lanzar 404)
        Dish dish = dishPersistentPort.findById(id)
                .orElseThrow(() -> new DishNotFoundException(DISH_NOT_FOUND_MESSAGE + id));

        // 4. Actualizar únicamente el campo que fue enviado
        if (price != null) {
            dish.setPrice(price);
        }
        if (description != null) {
            dish.setDescription(description);
        }

        return dishPersistentPort.save(dish);
    }

    @Override
    public Dish changeDishStatus(Long id, Boolean active, Long ownerId) {
        if (active == null) {
            throw new InvalidDishDataException(INVALID_STATUS_MESSAGE);
        }

        Dish dish = dishPersistentPort.findById(id)
                .orElseThrow(() -> new DishNotFoundException(DISH_NOT_FOUND_MESSAGE + id));

        Restaurant restaurant = restaurantPersistentPort.findById(dish.getIdRestaurant())
                .orElseThrow(() -> new RestaurantNotFoundException(INVALID_RESTAURANT_MESSAGE));

        if (ownerId == null || !restaurant.getIdOwner().equals(ownerId)) {
            throw new UserNotOwnerOfRestaurantException(NOT_OWNER_MESSAGE);
        }

        dish.setActive(active);
        return dishPersistentPort.save(dish);
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
