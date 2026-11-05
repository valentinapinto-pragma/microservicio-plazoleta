package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.OrderDishRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderDtoMapperTest {

    private final OrderDtoMapper mapper = Mappers.getMapper(OrderDtoMapper.class);

    @Test
    @DisplayName("Should map OrderRequestDto to Order domain model correctly")
    void shouldMapOrderRequestDtoToDomain() {
        OrderDishRequestDto dishDto = new OrderDishRequestDto(100L, 2);
        OrderRequestDto requestDto = new OrderRequestDto(5L, List.of(dishDto));

        Order domain = mapper.toDomain(requestDto);

        assertNotNull(domain);
        assertEquals(5L, domain.getRestaurantId());
        assertNotNull(domain.getOrderDishes());
        assertEquals(1, domain.getOrderDishes().size());
        assertEquals(100L, domain.getOrderDishes().get(0).getDishId());
        assertEquals(2, domain.getOrderDishes().get(0).getQuantity());
    }

    @Test
    @DisplayName("Should map OrderDishRequestDto to OrderDish domain model correctly")
    void shouldMapOrderDishRequestDtoToDomain() {
        OrderDishRequestDto dishDto = new OrderDishRequestDto(50L, 3);

        OrderDish domain = mapper.toDomain(dishDto);

        assertNotNull(domain);
        assertEquals(50L, domain.getDishId());
        assertEquals(3, domain.getQuantity());
    }

    @Test
    @DisplayName("Should return null when inputs are null")
    void shouldReturnNullWhenInputsAreNull() {
        assertNull(mapper.toDomain((OrderRequestDto) null));
        assertNull(mapper.toDomain((OrderDishRequestDto) null));
    }
}