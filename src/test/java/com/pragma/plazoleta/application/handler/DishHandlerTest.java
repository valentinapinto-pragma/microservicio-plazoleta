package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishStatusRequestDto;
import com.pragma.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.application.dto.response.PaginationResponseDto;
import com.pragma.plazoleta.application.mapper.DishDtoMapper;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.CustomPage;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.servicePort.DishServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishHandlerTest {

    @Mock
    private DishServicePort dishServicePort;

    @Mock
    private DishDtoMapper dishDtoMapper;

    @InjectMocks
    private DishHandler dishHandler;

    @Test
    @DisplayName("Should successfully map DTO to domain and call dish service port")
    void shouldCreateDishSuccessfully() {
        DishRequestDto requestDto = new DishRequestDto("Hamburguesa", 15000, "Carne y queso", "https://img.com/burger.png", 1L, 10L);
        Dish domainDish = new Dish(null, "Hamburguesa", 15000, "Carne y queso", "https://img.com/burger.png", new Category(1L, null, null), 10L, null);

        when(dishDtoMapper.toDomain(requestDto)).thenReturn(domainDish);

        dishHandler.createDish(requestDto);

        verify(dishDtoMapper).toDomain(requestDto);
        verify(dishServicePort).createDish(domainDish);
    }

    @Test
    @DisplayName("Should call dish service port on update dish")
    void shouldUpdateDishSuccessfully() {
        UpdateDishRequestDto updateDto = new UpdateDishRequestDto(35000, "Descripcion actualizada");

        dishHandler.updateDish(1L, updateDto);

        verify(dishServicePort).updateDish(1L, 35000, "Descripcion actualizada");
    }

    @Test
    @DisplayName("Should call dish service port on change dish status (HU-7)")
    void shouldChangeDishStatusSuccessfully() {
        DishStatusRequestDto statusDto = new DishStatusRequestDto(false);
        Long dishId = 1L;

        dishHandler.changeDishStatus(dishId, statusDto);

        verify(dishServicePort).changeDishStatus(dishId, false);
    }

    @Test
    @DisplayName("Should successfully get dishes by restaurant and map to DTO list")
    void shouldGetDishesByRestaurantSuccessfully() {
        Dish dish = new Dish(1L, "Hamburguesa", 15000, "Carne", "https://img.com/burger.png", new Category(1L, "Comida", null), 10L, true);
        DishResponseDto responseDto = new DishResponseDto(1L, "Hamburguesa", 15000, "Carne", "https://img.com/burger.png", "Comida");
        CustomPage<Dish> customPage = new CustomPage<>(List.of(dish), 0, 10, 1L, 1);

        when(dishServicePort.getDishesByRestaurant(10L, 1L, 0, 10)).thenReturn(customPage);
        when(dishDtoMapper.toResponseDtoList(List.of(dish))).thenReturn(List.of(responseDto));

        PaginationResponseDto<DishResponseDto> result = dishHandler.getDishesByRestaurant(10L, 1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Hamburguesa", result.getContent().get(0).getName());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(dishServicePort).getDishesByRestaurant(10L, 1L, 0, 10);
        verify(dishDtoMapper).toResponseDtoList(List.of(dish));
    }
}