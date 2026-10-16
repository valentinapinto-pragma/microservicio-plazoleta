package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
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
}
