package com.pragma.plazoleta.infrastructure.client.adapter;

import com.pragma.plazoleta.domain.model.RoleEnum;
import com.pragma.plazoleta.domain.repositoryPort.UserValidationPort;
import com.pragma.plazoleta.infrastructure.client.dto.UserResponseDto;
import com.pragma.plazoleta.infrastructure.client.feign.UserFeignClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidationAdapter implements UserValidationPort {

    private final UserFeignClient userFeignClient;

    @Override
    public boolean isValidOwner(Long ownerId) {
        try {
            UserResponseDto user = userFeignClient.getUserById(ownerId);
            return isOwnerRole(user);
        } catch (FeignException.NotFound e) {
            return false;
        }
    }

    private boolean isOwnerRole(UserResponseDto user) {
        if (user == null) {
            return false;
        }
        if (user.getRoleId() != null) {
            return RoleEnum.OWNER.getId().equals(user.getRoleId());
        }
        return user.getRole() != null && RoleEnum.OWNER.getId().equals(user.getRole().getId());
    }

}