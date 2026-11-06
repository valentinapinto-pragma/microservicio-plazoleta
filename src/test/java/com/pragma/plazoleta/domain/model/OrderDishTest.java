package com.pragma.plazoleta.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderDishTest {

    @Test
    @DisplayName("Should create OrderDish with full constructor and verify getters and setters")
    void shouldVerifyOrderDishGettersAndSetters() {
        // Arrange
        Dish dish = new Dish();
        dish.setId(100L);
        dish.setName("Hamburguesa");

        // Act
        OrderDish orderDish = new OrderDish(1L, 10L, 100L, 2, dish);

        // Assert
        assertEquals(1L, orderDish.getId());
        assertEquals(10L, orderDish.getOrderId());
        assertEquals(100L, orderDish.getDishId());
        assertEquals(2, orderDish.getQuantity());
        assertEquals(dish, orderDish.getDish());

        // Act (Setters)
        orderDish.setId(2L);
        orderDish.setOrderId(20L);
        orderDish.setDishId(200L);
        orderDish.setQuantity(3);
        Dish newDish = new Dish();
        orderDish.setDish(newDish);

        // Assert
        assertEquals(2L, orderDish.getId());
        assertEquals(20L, orderDish.getOrderId());
        assertEquals(200L, orderDish.getDishId());
        assertEquals(3, orderDish.getQuantity());
        assertEquals(newDish, orderDish.getDish());
    }

    @Test
    @DisplayName("Should create OrderDish with 4-args and no-args constructor")
    void shouldVerifyOtherConstructors() {
        // Arrange & Act
        OrderDish empty = new OrderDish();
        OrderDish fourArgs = new OrderDish(1L, 10L, 100L, 2);

        // Assert
        assertNotNull(empty);
        assertEquals(1L, fourArgs.getId());
        assertEquals(10L, fourArgs.getOrderId());
        assertEquals(100L, fourArgs.getDishId());
        assertEquals(2, fourArgs.getQuantity());
        assertNull(fourArgs.getDish());
    }
}