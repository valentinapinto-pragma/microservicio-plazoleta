package com.pragma.plazoleta.infrastructure.persistence.mapper;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.infrastructure.persistence.entity.OrderDishEntity;
import com.pragma.plazoleta.infrastructure.persistence.entity.OrderEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrderEntityMapper {

    @Mapping(source = "clientId", target = "idClient")
    @Mapping(source = "restaurantId", target = "idRestaurant")
    @Mapping(source = "chefId", target = "idChef")
    OrderEntity toEntity(Order order);

    @Mapping(source = "idClient", target = "clientId")
    @Mapping(source = "idRestaurant", target = "restaurantId")
    @Mapping(source = "idChef", target = "chefId")
    Order toDomain(OrderEntity orderEntity);

    @Mapping(source = "dishId", target = "idDish")
    @Mapping(target = "order", ignore = true)
    OrderDishEntity toEntity(OrderDish orderDish);

    @Mapping(source = "idDish", target = "dishId")
    @Mapping(source = "order.id", target = "orderId")
    OrderDish toDomain(OrderDishEntity orderDishEntity);

    @AfterMapping
    default void linkOrderDishes(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity.getOrderDishes() != null) {
            orderEntity.getOrderDishes().forEach(dishEntity -> dishEntity.setOrder(orderEntity));
        }
    }
}