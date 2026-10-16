package com.pragma.plazoleta.application.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void shouldPassValidationWithValidData() {
        RestaurantRequestDto dto = new RestaurantRequestDto(
                "Restaurante El 10",
                "900123456",
                "Calle 50 # 10-20",
                "+573001234567",
                "https://example.com/logo.png",
                1L
        );

        Set<ConstraintViolation<RestaurantRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when name consists only of numbers")
    void shouldFailWhenNameIsOnlyNumbers() {
        RestaurantRequestDto dto = new RestaurantRequestDto(
                "123456",
                "900123456",
                "Calle 50 # 10-20",
                "+573001234567",
                "https://example.com/logo.png",
                1L
        );

        Set<ConstraintViolation<RestaurantRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when fields are null or blank")
    void shouldFailWhenFieldsAreNullOrBlank() {
        RestaurantRequestDto dto = new RestaurantRequestDto();

        Set<ConstraintViolation<RestaurantRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 5);
    }

    @Test
    @DisplayName("Should set and get properties correctly")
    void shouldSetAndGetProperties() {
        RestaurantRequestDto dto = new RestaurantRequestDto();
        dto.setName("Sabor Paisa");
        dto.setNit("800123456");
        dto.setAddress("Circular 4 # 70-10");
        dto.setPhone("3111234567");
        dto.setUrlLogo("https://paisa.com/logo.png");
        dto.setIdOwner(3L);

        assertEquals("Sabor Paisa", dto.getName());
        assertEquals("800123456", dto.getNit());
        assertEquals("Circular 4 # 70-10", dto.getAddress());
        assertEquals("3111234567", dto.getPhone());
        assertEquals("https://paisa.com/logo.png", dto.getUrlLogo());
        assertEquals(3L, dto.getIdOwner());
    }
}
