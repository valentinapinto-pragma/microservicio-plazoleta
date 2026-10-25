package com.pragma.plazoleta.domain.exception;

public class UserNotOwnerOfRestaurantException extends RuntimeException {
  public UserNotOwnerOfRestaurantException(String message) {
    super(message);
  }
}
