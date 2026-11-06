package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.OrderDishRequestDto;
import com.pragma.plazoleta.application.dto.request.OrderRequestDto;
import com.pragma.plazoleta.application.dto.response.OrderDishResponseDto;
import com.pragma.plazoleta.application.dto.response.OrderResponseDto;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
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
    @DisplayName("Should map Order domain to OrderResponseDto correctly")
    void shouldMapOrderToResponseDto() {
        Category category = new Category(1L, "Comida Rápida", "Platos rápidos");
        Dish dish = new Dish(100L, "Hamburguesa", 25000, "Rica", "http://img.com/1", category, 2L, true);
        OrderDish orderDish = new OrderDish(1L, 10L, 100L, 2, dish);

        LocalDateTime now = LocalDateTime.now();
        Order order = new Order(10L, 20L, now, OrderStatus.PENDIENTE, 30L, 2L, List.of(orderDish));

        OrderResponseDto responseDto = mapper.toResponseDto(order);

        assertNotNull(responseDto);
        assertEquals(10L, responseDto.getId());
        assertEquals(20L, responseDto.getIdClient());
        assertEquals(now, responseDto.getDate());
        assertEquals(OrderStatus.PENDIENTE, responseDto.getStatus());
        assertEquals(30L, responseDto.getIdChef());
        assertEquals(2L, responseDto.getIdRestaurant());
        assertNotNull(responseDto.getDishes());
        assertEquals(1, responseDto.getDishes().size());

        OrderDishResponseDto dishResponseDto = responseDto.getDishes().get(0);
        assertEquals(100L, dishResponseDto.getIdDish());
        assertEquals("Hamburguesa", dishResponseDto.getName());
        assertEquals(25000, dishResponseDto.getPrice());
        assertEquals("Rica", dishResponseDto.getDescription());
        assertEquals("http://img.com/1", dishResponseDto.getUrlImage());
        assertEquals("Comida Rápida", dishResponseDto.getCategory());
        assertEquals(2, dishResponseDto.getQuantity());
    }

    @Test
    @DisplayName("Should map list of Order to list of OrderResponseDto")
    void shouldMapOrderListToResponseDtoList() {
        Order order = new Order(10L, 20L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 2L, List.of());

        List<OrderResponseDto> dtoList = mapper.toResponseDtoList(List.of(order));

        assertNotNull(dtoList);
        assertEquals(1, dtoList.size());
        assertEquals(10L, dtoList.get(0).getId());
    }

    @Test
    @DisplayName("Should return null when inputs are null")
    void shouldReturnNullWhenInputsAreNull() {
        assertNull(mapper.toDomain((OrderRequestDto) null));
        assertNull(mapper.toDomain((OrderDishRequestDto) null));
        assertNull(mapper.toResponseDto((Order) null));
        assertNull(mapper.toResponseDto((OrderDish) null));
        assertNull(mapper.toResponseDtoList(null));
    }
}