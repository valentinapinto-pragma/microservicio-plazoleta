package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishStatusRequestDto;
import com.pragma.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.pragma.plazoleta.application.dto.response.DishResponseDto;
import com.pragma.plazoleta.application.mapper.DishDtoMapper;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.servicePort.DishServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
public class DishHandler {

    private final DishServicePort dishServicePort;
    private final DishDtoMapper dishDtoMapper;

    public void createDish(DishRequestDto dishRequestDto) {
        dishServicePort.createDish(dishDtoMapper.toDomain(dishRequestDto));
    }

    public void updateDish(Long id, UpdateDishRequestDto updateDishRequestDto) {
        dishServicePort.updateDish(id, updateDishRequestDto.getPrice(), updateDishRequestDto.getDescription());
    }

    public void changeDishStatus(Long id, DishStatusRequestDto dishStatusRequestDto) {
        dishServicePort.changeDishStatus(id, dishStatusRequestDto.getActive());
    }

    @Transactional(readOnly = true)
    public List<DishResponseDto> getDishesByRestaurant(Long idRestaurant, Long categoryId, int page, int size) {
        List<Dish> dishes = dishServicePort.getDishesByRestaurant(idRestaurant, categoryId, page, size);
        return dishDtoMapper.toResponseDtoList(dishes);
    }
}