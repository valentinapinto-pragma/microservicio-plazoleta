package com.pragma.plazoleta.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private Long clientId;
    private LocalDateTime date;
    private OrderStatus status;
    private Long chefId;
    private Long restaurantId;
    private List<OrderDish> orderDishes;
    private String securityPin;

    public Order() {
    }

    public Order(Long id, Long clientId, LocalDateTime date, OrderStatus status, Long chefId, Long restaurantId, List<OrderDish> orderDishes) {
        this(id, clientId, date, status, chefId, restaurantId, orderDishes, null);
    }

    public Order(Long id, Long clientId, LocalDateTime date, OrderStatus status, Long chefId, Long restaurantId, List<OrderDish> orderDishes, String securityPin) {
        this.id = id;
        this.clientId = clientId;
        this.date = date;
        this.status = status;
        this.chefId = chefId;
        this.restaurantId = restaurantId;
        this.orderDishes = orderDishes;
        this.securityPin = securityPin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getChefId() {
        return chefId;
    }

    public void setChefId(Long chefId) {
        this.chefId = chefId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<OrderDish> getOrderDishes() {
        return orderDishes;
    }

    public void setOrderDishes(List<OrderDish> orderDishes) {
        this.orderDishes = orderDishes;
    }

    public String getSecurityPin() {
        return securityPin;
    }

    public void setSecurityPin(String securityPin) {
        this.securityPin = securityPin;
    }
}