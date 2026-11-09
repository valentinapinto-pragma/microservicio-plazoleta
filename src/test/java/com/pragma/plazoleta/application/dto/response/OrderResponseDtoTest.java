package com.pragma.plazoleta.application.dto.response;

import com.pragma.plazoleta.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseDtoTest {

    @Test
    @DisplayName("Should test OrderResponseDto and OrderDishResponseDto constructors, getters and setters")
    void shouldTestDtosGettersAndSetters() {
        // Arrange & Act
        OrderDishResponseDto dishDto = new OrderDishResponseDto(
                100L,
                "Hamburguesa",
                25000,
                "Rica hamburguesa",
                "http://img.com/1",
                "Comida Rápida",
                2
        );

        LocalDateTime now = LocalDateTime.now();
        OrderResponseDto orderDto = new OrderResponseDto(
                1L,
                10L,
                now,
                OrderStatus.PENDIENTE,
                5L,
                2L,
                List.of(dishDto)
        );

        // Assert
        assertEquals(100L, dishDto.getIdDish());
        assertEquals("Hamburguesa", dishDto.getName());
        assertEquals(25000, dishDto.getPrice());
        assertEquals("Rica hamburguesa", dishDto.getDescription());
        assertEquals("http://img.com/1", dishDto.getUrlImage());
        assertEquals("Comida Rápida", dishDto.getCategory());
        assertEquals(2, dishDto.getQuantity());

        assertEquals(1L, orderDto.getId());
        assertEquals(10L, orderDto.getIdClient());
        assertEquals(now, orderDto.getDate());
        assertEquals(OrderStatus.PENDIENTE, orderDto.getStatus());
        assertEquals(5L, orderDto.getIdChef());
        assertEquals(2L, orderDto.getIdRestaurant());
        assertEquals(1, orderDto.getDishes().size());
        assertNull(orderDto.getSecurityPin());

        // Test Setters & NoArgsConstructor & SecurityPin
        OrderResponseDto orderWithPin = new OrderResponseDto(
                1L, 10L, now, OrderStatus.LISTO, 5L, 2L, List.of(dishDto), "123456"
        );
        assertEquals("123456", orderWithPin.getSecurityPin());

        OrderDishResponseDto emptyDish = new OrderDishResponseDto();
        emptyDish.setIdDish(200L);
        emptyDish.setName("Pizza");
        assertEquals(200L, emptyDish.getIdDish());
        assertEquals("Pizza", emptyDish.getName());

        OrderResponseDto emptyOrder = new OrderResponseDto();
        emptyOrder.setId(2L);
        emptyOrder.setStatus(OrderStatus.EN_PREPARACION);
        emptyOrder.setSecurityPin("654321");
        assertEquals(2L, emptyOrder.getId());
        assertEquals(OrderStatus.EN_PREPARACION, emptyOrder.getStatus());
        assertEquals("654321", emptyOrder.getSecurityPin());
    }
}