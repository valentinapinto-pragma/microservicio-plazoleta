package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishRequestDto;
import com.pragma.plazoleta.application.dto.request.DishStatusRequestDto;
import com.pragma.plazoleta.application.dto.request.UpdateDishRequestDto;
import com.pragma.plazoleta.application.mapper.DishDtoMapper;
import com.pragma.plazoleta.domain.servicePort.DishServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

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
}