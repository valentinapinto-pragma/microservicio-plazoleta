package com.pragma.plazoleta.infrastructure.persistence.mapper;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.infrastructure.persistence.entity.OrderDishEntity;
import com.pragma.plazoleta.infrastructure.persistence.entity.OrderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityMapperTest {

    private final OrderEntityMapper mapper = Mappers.getMapper(OrderEntityMapper.class);

    @Test
    @DisplayName("Should map Order domain to OrderEntity and link order dishes correctly")
    void shouldMapDomainToEntity() {
        List<OrderDish> dishes = new ArrayList<>();
        dishes.add(new OrderDish(1L, null, 100L, 2));

        Order domain = new Order(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, 5L, 2L, dishes);

        OrderEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getClientId(), entity.getIdClient());
        assertEquals(domain.getRestaurantId(), entity.getIdRestaurant());
        assertEquals(domain.getChefId(), entity.getIdChef());
        assertEquals(domain.getStatus(), entity.getStatus());
        assertEquals(1, entity.getOrderDishes().size());
        assertEquals(entity, entity.getOrderDishes().get(0).getOrder()); // Verifica el @AfterMapping
    }

    @Test
    @DisplayName("Should map OrderEntity to Order domain correctly")
    void shouldMapEntityToDomain() {
        OrderEntity entity = new OrderEntity(1L, 10L, LocalDateTime.now(), OrderStatus.PENDIENTE, 5L, 2L, new ArrayList<>());

        Order domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getIdClient(), domain.getClientId());
        assertEquals(entity.getIdRestaurant(), domain.getRestaurantId());
        assertEquals(entity.getIdChef(), domain.getChefId());
    }

    @Test
    @DisplayName("Should map OrderDish domain and entity correctly")
    void shouldMapOrderDish() {
        OrderDish dishDomain = new OrderDish(1L, 10L, 100L, 2);
        OrderDishEntity dishEntity = mapper.toEntity(dishDomain);

        assertNotNull(dishEntity);
        assertEquals(100L, dishEntity.getIdDish());
        assertEquals(2, dishEntity.getQuantity());

        OrderEntity parentOrder = new OrderEntity();
        parentOrder.setId(10L);
        dishEntity.setOrder(parentOrder);

        OrderDish mappedBack = mapper.toDomain(dishEntity);
        assertNotNull(mappedBack);
        assertEquals(10L, mappedBack.getOrderId());
        assertEquals(100L, mappedBack.getDishId());
    }

    @Test
    @DisplayName("Should return null when inputs are null")
    void shouldReturnNullWhenInputsAreNull() {
        assertNull(mapper.toEntity((Order) null));
        assertNull(mapper.toDomain((OrderEntity) null));
        assertNull(mapper.toEntity((OrderDish) null));
        assertNull(mapper.toDomain((OrderDishEntity) null));
    }
}