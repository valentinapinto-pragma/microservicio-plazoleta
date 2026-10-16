package com.pragma.plazoleta.infrastructure.client.adapter;

import com.pragma.plazoleta.infrastructure.client.dto.RoleResponseDto;
import com.pragma.plazoleta.infrastructure.client.dto.UserResponseDto;
import com.pragma.plazoleta.infrastructure.client.feign.UserFeignClient;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidationAdapterTest {

    @Mock
    private UserFeignClient userFeignClient;

    @InjectMocks
    private UserValidationAdapter userValidationAdapter;

    @Test
    @DisplayName("Should return true when user has owner role via roleId")
    void shouldReturnTrueWhenUserHasOwnerRoleId() {
        UserResponseDto user = new UserResponseDto();
        user.setId(2L);
        user.setRoleId(2L);

        when(userFeignClient.getUserById(2L)).thenReturn(user);

        boolean result = userValidationAdapter.isValidOwner(2L);

        assertTrue(result);
        verify(userFeignClient).getUserById(2L);
    }

    @Test
    @DisplayName("Should return true when user has owner role via nested role object")
    void shouldReturnTrueWhenUserHasOwnerNestedRole() {
        RoleResponseDto role = new RoleResponseDto(2L, "PROPIETARIO", "Rol propietario");
        UserResponseDto user = new UserResponseDto();
        user.setId(2L);
        user.setRole(role);

        when(userFeignClient.getUserById(2L)).thenReturn(user);

        boolean result = userValidationAdapter.isValidOwner(2L);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when user has different role")
    void shouldReturnFalseWhenUserHasDifferentRole() {
        UserResponseDto user = new UserResponseDto();
        user.setId(3L);
        user.setRoleId(1L);

        when(userFeignClient.getUserById(3L)).thenReturn(user);

        boolean result = userValidationAdapter.isValidOwner(3L);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when feign client returns null")
    void shouldReturnFalseWhenUserIsNull() {
        when(userFeignClient.getUserById(4L)).thenReturn(null);

        boolean result = userValidationAdapter.isValidOwner(4L);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when FeignException.NotFound is thrown")
    void shouldReturnFalseWhenUserNotFoundExceptionThrown() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/users/99",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        when(userFeignClient.getUserById(99L)).thenThrow(new FeignException.NotFound("User not found", request, null, null));

        boolean result = userValidationAdapter.isValidOwner(99L);

        assertFalse(result);
    }
}
