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

class UpdateDishRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should pass validation when update data is valid")
    void shouldPassValidationWhenValid() {
        UpdateDishRequestDto dto = new UpdateDishRequestDto(32000, "Nueva descripción deliciosa");

        Set<ConstraintViolation<UpdateDishRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when update price is negative or zero")
    void shouldFailWhenPriceIsNegativeOrZero() {
        UpdateDishRequestDto dto = new UpdateDishRequestDto(0, "Nueva descripción");

        Set<ConstraintViolation<UpdateDishRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when update description is blank or null")
    void shouldFailWhenDescriptionIsBlank() {
        UpdateDishRequestDto dto = new UpdateDishRequestDto(25000, "   ");

        Set<ConstraintViolation<UpdateDishRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should test getters and setters")
    void testGettersAndSetters() {
        UpdateDishRequestDto dto = new UpdateDishRequestDto();
        dto.setPrice(15000);
        dto.setDescription("Descripción");

        assertEquals(15000, dto.getPrice());
        assertEquals("Descripción", dto.getDescription());
    }
}
