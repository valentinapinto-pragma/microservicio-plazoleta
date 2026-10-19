package com.pragma.plazoleta.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.pragma.plazoleta.application.handler.DishHandler;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidDishDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.infrastructure.exceptionhandler.ControllerAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DishControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DishHandler dishHandler;

    @InjectMocks
    private DishController dishController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dishController)
                .setControllerAdvice(new ControllerAdvisor())
                .build();
    }

    @Test
    @DisplayName("Should return 201 Created when dish data is valid")
    void shouldReturnCreatedWhenValid() throws Exception {
        DishRequestDto dto = new DishRequestDto("Hamburguesa", 15000, "Deliciosa carne", "https://img.com/burger.png", 1L, 1L);

        doNothing().when(dishHandler).createDish(any(DishRequestDto.class));

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Dish created successfully"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when validation fails")
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        DishRequestDto invalidDto = new DishRequestDto();

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when restaurant does not exist")
    void shouldReturnBadRequestWhenRestaurantNotFound() throws Exception {
        DishRequestDto dto = new DishRequestDto("Hamburguesa", 15000, "Carne", "https://img.com/burger.png", 1L, 999L);

        doThrow(new RestaurantNotFoundException("The restaurant associated with the dish does not exist"))
                .when(dishHandler).createDish(any(DishRequestDto.class));

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The restaurant associated with the dish does not exist"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when price or data is invalid")
    void shouldReturnBadRequestWhenInvalidDishData() throws Exception {
        DishRequestDto dto = new DishRequestDto("Hamburguesa", 15000, "Carne", "https://img.com/burger.png", 1L, 1L);

        doThrow(new InvalidDishDataException("The dish price must be an integer greater than 0"))
                .when(dishHandler).createDish(any(DishRequestDto.class));

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The dish price must be an integer greater than 0"));
    }
    @Test
    @DisplayName("Should return 200 OK when dish is updated successfully")
    void shouldReturnOkWhenDishUpdated() throws Exception {
        UpdateDishRequestDto updateDto = new UpdateDishRequestDto(32000, "Nueva descripción");

        doNothing().when(dishHandler).updateDish(eq(1L), any(UpdateDishRequestDto.class));

        mockMvc.perform(patch("/dishes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Dish updated successfully"));
    }

    @Test
    @DisplayName("Should return 404 Not Found when dish to update does not exist")
    void shouldReturnNotFoundWhenDishToUpdateDoesNotExist() throws Exception {
        UpdateDishRequestDto updateDto = new UpdateDishRequestDto(32000, "Nueva descripción");

        doThrow(new DishNotFoundException("Dish not found with id: 99"))
                .when(dishHandler).updateDish(eq(99L), any(UpdateDishRequestDto.class));

        mockMvc.perform(patch("/dishes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Dish not found with id: 99"));
    }

}
