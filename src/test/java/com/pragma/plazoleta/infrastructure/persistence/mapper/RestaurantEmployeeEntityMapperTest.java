package com.pragma.plazoleta.infrastructure.persistence.mapper;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.infrastructure.persistence.entity.RestaurantEmployeeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantEmployeeEntityMapperTest {

    private final RestaurantEmployeeEntityMapper mapper = Mappers.getMapper(RestaurantEmployeeEntityMapper.class);

    @Test
    @DisplayName("Should map domain to entity correctly")
    void shouldMapDomainToEntity() {
        RestaurantEmployee domain = new RestaurantEmployee(1L, 10L, 20L);

        RestaurantEmployeeEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(10L, entity.getRestaurantId());
        assertEquals(20L, entity.getEmployeeId());
    }

    @Test
    @DisplayName("Should map entity to domain correctly")
    void shouldMapEntityToDomain() {
        RestaurantEmployeeEntity entity = new RestaurantEmployeeEntity(1L, 10L, 20L);

        RestaurantEmployee domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1L, domain.getId());
        assertEquals(10L, domain.getRestaurantId());
        assertEquals(20L, domain.getEmployeeId());
    }

    @Test
    @DisplayName("Should return null when mapping null objects")
    void shouldReturnNullWhenMappingNull() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDomain(null));
    }
}