package com.pragma.plazoleta.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishStatusRequestDto;
import com.pragma.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.pragma.plazoleta.application.handler.DishHandler;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidDishDataException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.UserNotOwnerOfRestaurantException;
import com.pragma.plazoleta.infrastructure.exceptionhandler.ControllerAdvisor;
import com.pragma.plazoleta.infrastructure.security.JwtAdapter;
import com.pragma.plazoleta.infrastructure.security.SecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DishController.class)
@Import({SecurityConfiguration.class, ControllerAdvisor.class})
@WithMockUser(roles = "OWNER")
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DishHandler dishHandler;

    @MockBean
    private JwtAdapter jwtAdapter;

    @Test
    @DisplayName("Should return 201 Created when dish data is valid")
    void shouldReturnCreatedWhenValid() throws Exception {
        DishRequestDto dto = new DishRequestDto("Hamburguesa", 15000, "Deliciosa carne", "https://img.com/burger.png", 1L, 1L);

        doNothing().when(dishHandler).createDish(any(DishRequestDto.class));

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(dishHandler).createDish(any(DishRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when validation fails")
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        DishRequestDto invalidDto = new DishRequestDto();

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(dishHandler, never()).createDish(any());
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
        UpdateDishRequestDto updateDto = new UpdateDishRequestDto(32000, "Nueva descripcion");

        doNothing().when(dishHandler).updateDish(eq(1L), any(UpdateDishRequestDto.class));

        mockMvc.perform(patch("/dishes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        verify(dishHandler).updateDish(eq(1L), any(UpdateDishRequestDto.class));
    }

    @Test
    @DisplayName("Should return 404 Not Found when dish to update does not exist")
    void shouldReturnNotFoundWhenDishToUpdateDoesNotExist() throws Exception {
        UpdateDishRequestDto updateDto = new UpdateDishRequestDto(32000, "Nueva descripcion");

        doThrow(new DishNotFoundException("Dish not found with id: 99"))
                .when(dishHandler).updateDish(eq(99L), any(UpdateDishRequestDto.class));

        mockMvc.perform(patch("/dishes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Dish not found with id: 99"));
    }

    @Test
    @DisplayName("Should return 200 OK when dish status is changed successfully by restaurant owner")
    void shouldReturnOkWhenDishStatusChangedSuccessfully() throws Exception {
        DishStatusRequestDto statusDto = new DishStatusRequestDto(false);

        doNothing().when(dishHandler).changeDishStatus(eq(1L), any(DishStatusRequestDto.class));

        mockMvc.perform(patch("/dishes/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk());

        verify(dishHandler).changeDishStatus(eq(1L), any(DishStatusRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 Forbidden when role is not owner on status change")
    void shouldReturnForbiddenWhenRoleIsNotOwnerOnStatusChange() throws Exception {
        DishStatusRequestDto statusDto = new DishStatusRequestDto(false);

        mockMvc.perform(patch("/dishes/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isForbidden());

        verify(dishHandler, never()).changeDishStatus(any(), any());
    }

    @Test
    @DisplayName("Should return 403 Forbidden when user is not the owner of the restaurant")
    void shouldReturnForbiddenWhenUserNotOwnerOfRestaurant() throws Exception {
        DishStatusRequestDto statusDto = new DishStatusRequestDto(false);

        doThrow(new UserNotOwnerOfRestaurantException("The user is not the owner of the restaurant to which the dish belongs"))
                .when(dishHandler).changeDishStatus(eq(1L), any(DishStatusRequestDto.class));

        mockMvc.perform(patch("/dishes/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("The user is not the owner of the restaurant to which the dish belongs"));
    }

    @Test
    @DisplayName("Should return 404 Not Found when dish to change status is not found")
    void shouldReturnNotFoundWhenDishToChangeStatusDoesNotExist() throws Exception {
        DishStatusRequestDto statusDto = new DishStatusRequestDto(false);

        doThrow(new DishNotFoundException("Dish not found with id: 99"))
                .when(dishHandler).changeDishStatus(eq(99L), any(DishStatusRequestDto.class));

        mockMvc.perform(patch("/dishes/99/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Dish not found with id: 99"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when status DTO has null active field")
    void shouldReturnBadRequestWhenStatusDtoIsInvalid() throws Exception {
        DishStatusRequestDto invalidDto = new DishStatusRequestDto(null);

        mockMvc.perform(patch("/dishes/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}