package com.pragma.plazoleta.infrastructure.persistence.repository;

import com.pragma.plazoleta.infrastructure.persistence.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends JpaRepository<DishEntity, Long> {

    Page<DishEntity> findByIdRestaurantAndActiveTrue(Long idRestaurant, Pageable pageable);
    //filtra por categoría
    Page<DishEntity> findByIdRestaurantAndCategoryIdAndActiveTrue(Long idRestaurant, Long categoryId, Pageable pageable);
}
