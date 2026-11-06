package com.pragma.plazoleta.infrastructure.persistence.repository;

import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    boolean existsByIdClientAndStatusIn(Long idClient, List<OrderStatus> statuses);
    Page<OrderEntity> findByIdRestaurantAndStatus(Long idRestaurant, OrderStatus status, Pageable pageable);
}