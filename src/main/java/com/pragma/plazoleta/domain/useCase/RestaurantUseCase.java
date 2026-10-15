package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.InvalidRestaurantDataException;
import com.pragma.plazoleta.domain.exception.OwnerNotValidException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserValidationPort;
import com.pragma.plazoleta.domain.servicePort.RestaurantServicePort;

import java.util.regex.Pattern;

public class RestaurantUseCase implements RestaurantServicePort {

    private static final Pattern ONLY_NUMBERS_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{1,13}$");
    private static final Pattern NIT_PATTERN = Pattern.compile("^\\d+$");

    private static final String INVALID_NAME_MESSAGE = "The restaurant name cannot consist solely of numbers";
    private static final String INVALID_PHONE_MESSAGE = "The phone number must be numeric, max 13 characters, and may start with '+'";
    private static final String INVALID_NIT_MESSAGE = "The NIT must be strictly numeric";
    private static final String DUPLICATE_NIT_MESSAGE = "A restaurant with the provided NIT already exists";
    private static final String INVALID_OWNER_MESSAGE = "The user specified as owner does not exist or does not possess the OWNER role";

    private final RestaurantPersistentPort restaurantPersistentPort;
    private final UserValidationPort userValidationPort;

    public RestaurantUseCase(RestaurantPersistentPort restaurantPersistentPort, UserValidationPort userValidationPort) {
        this.restaurantPersistentPort = restaurantPersistentPort;
        this.userValidationPort = userValidationPort;
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        validateRestaurantData(restaurant);
        validateOwner(restaurant.getIdOwner());
        validateNitNotDuplicated(restaurant.getNit());
        return restaurantPersistentPort.saveRestaurant(restaurant);
    }

    private void validateRestaurantData(Restaurant restaurant) {
        validateName(restaurant.getName());
        validateNit(restaurant.getNit());
        validatePhone(restaurant.getPhone());
    }

    private void validateName(String name) {
        if (name == null || ONLY_NUMBERS_PATTERN.matcher(name.trim()).matches()) {
            throw new InvalidRestaurantDataException(INVALID_NAME_MESSAGE);
        }
    }

    private void validateNit(String nit) {
        if (nit == null || !NIT_PATTERN.matcher(nit.trim()).matches()) {
            throw new InvalidRestaurantDataException(INVALID_NIT_MESSAGE);
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new InvalidRestaurantDataException(INVALID_PHONE_MESSAGE);
        }
    }

    private void validateOwner(Long ownerId) {
        if (ownerId == null || !userValidationPort.isValidOwner(ownerId)) {
            throw new OwnerNotValidException(INVALID_OWNER_MESSAGE);
        }
    }

    private void validateNitNotDuplicated(String nit) {
        if (restaurantPersistentPort.existsByNit(nit)) {
            throw new InvalidRestaurantDataException(DUPLICATE_NIT_MESSAGE);
        }
    }
}
