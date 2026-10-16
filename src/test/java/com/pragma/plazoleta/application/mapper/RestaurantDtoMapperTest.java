package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.RestaurantRequestDto;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantDtoMapperTest {

    private final RestaurantDtoMapper mapper = Mappers.getMapper(RestaurantDtoMapper.class);

    @Test
    @DisplayName("Should correctly map RestaurantRequestDto to Restaurant domain model")
    void shouldMapRequestDtoToDomain() {
        RestaurantRequestDto dto = new RestaurantRequestDto(
                "Restaurante Central",
                "900123456",
                "Carrera 15 # 80-20",
                "+573109876543",
                "https://logo.com/logo.jpg",
                5L
        );

        Restaurant result = mapper.toDomain(dto);

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getNit(), result.getNit());
        assertEquals(dto.getAddress(), result.getAddress());
        assertEquals(dto.getPhone(), result.getPhone());
        assertEquals(dto.getUrlLogo(), result.getUrlLogo());
        assertEquals(dto.getIdOwner(), result.getIdOwner());
    }

    @Test
    @DisplayName("Should return null when mapping null dto")
    void shouldReturnNullWhenDtoIsNull() {
        assertNull(mapper.toDomain(null));
    }
}
