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

class DishRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void shouldPassValidationWhenValid() {
        DishRequestDto dto = new DishRequestDto(
                "Sushi",
                30000,
                "Roll de salmón fresco",
                "https://img.com/sushi.png",
                3L,
                1L
        );

        Set<ConstraintViolation<DishRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when price is negative or zero")
    void shouldFailWhenPriceIsNegativeOrZero() {
        DishRequestDto dto = new DishRequestDto(
                "Sushi",
                0,
                "Roll de salmón fresco",
                "https://img.com/sushi.png",
                3L,
                1L
        );

        Set<ConstraintViolation<DishRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when fields are blank or null")
    void shouldFailWhenFieldsAreNull() {
        DishRequestDto dto = new DishRequestDto();

        Set<ConstraintViolation<DishRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 5);
    }

    @Test
    @DisplayName("Should test getters and setters")
    void testGettersAndSetters() {
        DishRequestDto dto = new DishRequestDto();
        dto.setName("Tacos");
        dto.setPrice(18000);
        dto.setDescription("Tacos al pastor");
        dto.setUrlImage("https://img.com/tacos.png");
        dto.setIdCategory(4L);
        dto.setIdRestaurant(2L);

        assertEquals("Tacos", dto.getName());
        assertEquals(18000, dto.getPrice());
        assertEquals("Tacos al pastor", dto.getDescription());
        assertEquals("https://img.com/tacos.png", dto.getUrlImage());
        assertEquals(4L, dto.getIdCategory());
        assertEquals(2L, dto.getIdRestaurant());
    }
}
