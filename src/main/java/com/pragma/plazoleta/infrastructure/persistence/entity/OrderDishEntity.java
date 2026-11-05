package com.pragma.plazoleta.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "orders_dishes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    private OrderEntity order;

    @Column(name = "id_dish", nullable = false)
    private Long idDish;

    @Column(nullable = false)
    private Integer quantity;
}