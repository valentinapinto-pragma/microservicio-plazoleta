package com.pragma.plazoleta.domain.exception;

public class UserNotOwnerOfRestaurantException extends ForbiddenOperationException {
  public UserNotOwnerOfRestaurantException(String message) {
    super(message);
  }
}
