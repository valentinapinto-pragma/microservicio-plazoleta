package com.pragma.plazoleta.infrastructure.persistence.mapper;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.persistence.entity.RestaurantEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantEntityMapperTest {

    private final RestaurantEntityMapper mapper = Mappers.getMapper(RestaurantEntityMapper.class);

    @Test
    @DisplayName("Should map Restaurant domain model to RestaurantEntity correctly")
    void shouldMapDomainToEntity() {
        Restaurant domain = new Restaurant(
                1L,
                "Sabor Criollo",
                "900111222",
                "Calle 50 # 10",
                "+573001112233",
                "https://logo.png",
                2L
        );

        RestaurantEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getName(), entity.getName());
        assertEquals(domain.getNit(), entity.getNit());
        assertEquals(domain.getAddress(), entity.getAddress());
        assertEquals(domain.getPhone(), entity.getPhone());
        assertEquals(domain.getUrlLogo(), entity.getUrlLogo());
        assertEquals(domain.getIdOwner(), entity.getIdOwner());
    }

    @Test
    @DisplayName("Should map RestaurantEntity to Restaurant domain model correctly")
    void shouldMapEntityToDomain() {
        RestaurantEntity entity = new RestaurantEntity(
                1L,
                "Sabor Criollo",
                "900111222",
                "Calle 50 # 10",
                "+573001112233",
                "https://logo.png",
                2L
        );

        Restaurant domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getName(), domain.getName());
        assertEquals(entity.getNit(), domain.getNit());
        assertEquals(entity.getAddress(), domain.getAddress());
        assertEquals(entity.getPhone(), domain.getPhone());
        assertEquals(entity.getUrlLogo(), domain.getUrlLogo());
        assertEquals(entity.getIdOwner(), domain.getIdOwner());
    }

    @Test
    @DisplayName("Should return null when domain or entity input is null")
    void shouldReturnNullWhenInputsAreNull() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDomain(null));
    }
}
