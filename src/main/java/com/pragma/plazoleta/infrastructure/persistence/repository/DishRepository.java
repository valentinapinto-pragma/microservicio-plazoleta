package com.pragma.plazoleta.infrastructure.persistence.repository;

import com.pragma.plazoleta.infrastructure.persistence.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends JpaRepository<DishEntity, Long> {
}
