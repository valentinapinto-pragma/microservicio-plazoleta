package com.pragma.plazoleta.application.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderRequestDtoTest {

    @Test
    @DisplayName("Should test OrderRequestDto and OrderDishRequestDto constructors, getters and setters")
    void testDtos() {
        OrderDishRequestDto dishDto = new OrderDishRequestDto(100L, 2);
        assertEquals(100L, dishDto.getIdDish());
        assertEquals(2, dishDto.getQuantity());

        OrderDishRequestDto emptyDish = new OrderDishRequestDto();
        emptyDish.setIdDish(200L);
        emptyDish.setQuantity(5);
        assertEquals(200L, emptyDish.getIdDish());
        assertEquals(5, emptyDish.getQuantity());

        List<OrderDishRequestDto> list = new ArrayList<>();
        list.add(dishDto);

        OrderRequestDto requestDto = new OrderRequestDto(1L, list);
        assertEquals(1L, requestDto.getIdRestaurant());
        assertEquals(1, requestDto.getDishes().size());

        OrderRequestDto emptyRequest = new OrderRequestDto();
        emptyRequest.setIdRestaurant(2L);
        emptyRequest.setDishes(list);
        assertEquals(2L, emptyRequest.getIdRestaurant());
        assertEquals(list, emptyRequest.getDishes());
    }
}