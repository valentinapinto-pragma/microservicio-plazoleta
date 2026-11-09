package com.pragma.plazoleta.domain.useCase;

import com.pragma.plazoleta.domain.exception.EmployeeAlreadyAssignedException;
import com.pragma.plazoleta.domain.exception.InvalidEmployeeException;
import com.pragma.plazoleta.domain.exception.InvalidRestaurantDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotOwnerOfRestaurantException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantEmployeePersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.RestaurantPersistentPort;
import com.pragma.plazoleta.domain.repositoryPort.UserInformationPort;
import com.pragma.plazoleta.domain.servicePort.RestaurantEmployeeServicePort;

public class RestaurantEmployeeUseCase implements RestaurantEmployeeServicePort {

    private final RestaurantPersistentPort restaurantPersistentPort;
    private final RestaurantEmployeePersistentPort restaurantEmployeePersistentPort;
    private final UserInformationPort userInformationPort;

    public RestaurantEmployeeUseCase(RestaurantPersistentPort restaurantPersistentPort,
                                     RestaurantEmployeePersistentPort restaurantEmployeePersistentPort,
                                     UserInformationPort userInformationPort) {
        this.restaurantPersistentPort = restaurantPersistentPort;
        this.restaurantEmployeePersistentPort = restaurantEmployeePersistentPort;
        this.userInformationPort = userInformationPort;
    }

    @Override
    public void assignEmployeeToRestaurant(Long restaurantId, Long employeeId) {
        validateInputs(restaurantId, employeeId);
        Long authenticatedOwnerId = userInformationPort.getAuthenticatedUserId();
        validateRestaurantOwnership(restaurantId, authenticatedOwnerId);
        validateEmployeeRole(employeeId);
        validateEmployeeNotAlreadyAssigned(employeeId);

        RestaurantEmployee restaurantEmployee = new RestaurantEmployee(null, restaurantId, employeeId);
        restaurantEmployeePersistentPort.save(restaurantEmployee);
    }

    private void validateInputs(Long restaurantId, Long employeeId) {
        if (restaurantId == null || employeeId == null) {
            throw new InvalidRestaurantDataException("Restaurant id and employee id must not be null");
        }
    }

    private void validateRestaurantOwnership(Long restaurantId, Long authenticatedOwnerId) {
        Restaurant restaurant = restaurantPersistentPort.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + restaurantId));

        if (!restaurant.getIdOwner().equals(authenticatedOwnerId)) {
            throw new UserNotOwnerOfRestaurantException("The user is not the owner of the restaurant");
        }
    }

    private void validateEmployeeRole(Long employeeId) {
        if (!userInformationPort.isValidEmployee(employeeId)) {
            throw new InvalidEmployeeException("The user is not a valid employee");
        }
    }

    private void validateEmployeeNotAlreadyAssigned(Long employeeId) {
        if (restaurantEmployeePersistentPort.findByEmployeeId(employeeId).isPresent()) {
            throw new EmployeeAlreadyAssignedException("The employee is already assigned to a restaurant");
        }
    }
}