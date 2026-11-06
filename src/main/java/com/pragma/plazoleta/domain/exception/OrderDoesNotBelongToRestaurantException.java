package com.pragma.plazoleta.domain.exception;

public class OrderDoesNotBelongToRestaurantException extends RuntimeException {
  public OrderDoesNotBelongToRestaurantException(String message) {
    super(message);
  }
}
