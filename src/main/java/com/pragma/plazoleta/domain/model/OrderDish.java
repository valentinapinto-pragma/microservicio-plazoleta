package com.pragma.plazoleta.domain.model;

public class OrderDish {
    private Long id;
    private Long orderId;
    private Long dishId;
    private Integer quantity;
    private Dish dish;

    public OrderDish() {
    }

    public OrderDish(Long id, Long orderId, Long dishId, Integer quantity) {
        this.id = id;
        this.orderId = orderId;
        this.dishId = dishId;
        this.quantity = quantity;
    }

    public OrderDish(Long id, Long orderId, Long dishId, Integer quantity, Dish dish) {
        this.id = id;
        this.orderId = orderId;
        this.dishId = dishId;
        this.quantity = quantity;
        this.dish = dish;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }
}