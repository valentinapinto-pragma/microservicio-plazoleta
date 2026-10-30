package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishStatusRequestDto;
import com.pragma.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.pragma.plazoleta.application.mapper.DishDtoMapper;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.servicePort.DishServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
