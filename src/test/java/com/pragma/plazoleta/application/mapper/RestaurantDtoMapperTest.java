package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.RestaurantRequestDto;
import com.pragma.plazoleta.application.dto.response.RestaurantResponseDto;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

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

    @Test
    @DisplayName("Should correctly map Restaurant to RestaurantResponseDto")
    void shouldMapDomainToResponseDto() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Central", "900123456", "Carrera 15", "+573109876543", "https://logo.com/logo.jpg", 5L);

        RestaurantResponseDto responseDto = mapper.toResponseDto(restaurant);

        assertNotNull(responseDto);
        assertEquals("Restaurante Central", responseDto.getName());
        assertEquals("https://logo.com/logo.jpg", responseDto.getUrlLogo());
    }

    @Test
    @DisplayName("Should return null when restaurant is null")
    void shouldReturnNullWhenRestaurantIsNull() {
        assertNull(mapper.toResponseDto(null));
    }

    @Test
    @DisplayName("Should map list of Restaurant to list of RestaurantResponseDto")
    void shouldMapDomainListToResponseDtoList() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Central", "900123456", "Carrera 15", "+573109876543", "https://logo.com/logo.jpg", 5L);

        List<RestaurantResponseDto> responseList = mapper.toResponseDtoList(List.of(restaurant));

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals("Restaurante Central", responseList.get(0).getName());
    }

    @Test
    @DisplayName("Should return null when restaurant list is null")
    void shouldReturnNullWhenRestaurantListIsNull() {
        assertNull(mapper.toResponseDtoList(null));
    }
}