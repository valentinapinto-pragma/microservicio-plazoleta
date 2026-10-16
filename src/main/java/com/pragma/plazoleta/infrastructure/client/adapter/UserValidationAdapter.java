package com.pragma.plazoleta.infrastructure.client.adapter;

import com.pragma.plazoleta.domain.repositoryPort.UserValidationPort;
import com.pragma.plazoleta.infrastructure.client.dto.UserResponseDto;
import com.pragma.plazoleta.infrastructure.client.feign.UserFeignClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidationAdapter implements UserValidationPort {

    private static final Long OWNER_ROLE_ID = 2L;
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
            return OWNER_ROLE_ID.equals(user.getRoleId());
        }
        return user.getRole() != null && OWNER_ROLE_ID.equals(user.getRole().getId());
    }
}
