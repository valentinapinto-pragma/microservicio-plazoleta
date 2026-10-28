package com.pragma.plazoleta.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.RestaurantRequestDto;
import com.pragma.plazoleta.application.handler.RestaurantHandler;
import com.pragma.plazoleta.domain.exception.InvalidRestaurantDataException;
import com.pragma.plazoleta.domain.exception.OwnerNotValidException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestaurantHandler restaurantHandler;

    @InjectMocks
    private RestaurantController restaurantController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController)
                .setControllerAdvice(new ControllerAdvisor())
                .build();
    }

    @Test
    @DisplayName("Should return 201 Created when request data is valid")
    void shouldReturnCreatedWhenDataIsValid() throws Exception {
        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Restaurante La 80",
                "123456789",
                "Calle 10 # 5-20",
                "+573001234567",
                "https://imagen.com/logo.png",
                2L
        );

        doNothing().when(restaurantHandler).createRestaurant(any(RestaurantRequestDto.class));

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(restaurantHandler).createRestaurant(any(RestaurantRequestDto.class));
    }


    @Test
    @DisplayName("Should return 400 Bad Request when validation fails")
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        RestaurantRequestDto invalidDto = new RestaurantRequestDto();

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(restaurantHandler, never()).createRestaurant(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when handler throws InvalidRestaurantDataException")
    void shouldReturnBadRequestWhenInvalidRestaurantData() throws Exception {
        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Restaurante 10",
                "123456789",
                "Calle 10",
                "+573001234567",
                "https://logo.png",
                2L
        );

        doThrow(new InvalidRestaurantDataException("The restaurant name cannot consist solely of numbers"))
                .when(restaurantHandler).createRestaurant(any(RestaurantRequestDto.class));

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The restaurant name cannot consist solely of numbers"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when handler throws OwnerNotValidException")
    void shouldReturnBadRequestWhenOwnerNotValid() throws Exception {
        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Restaurante 10",
                "123456789",
                "Calle 10",
                "+573001234567",
                "https://logo.png",
                99L
        );

        doThrow(new OwnerNotValidException("The user specified as owner does not exist or does not possess the OWNER role"))
                .when(restaurantHandler).createRestaurant(any(RestaurantRequestDto.class));

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The user specified as owner does not exist or does not possess the OWNER role"));
    }
}
