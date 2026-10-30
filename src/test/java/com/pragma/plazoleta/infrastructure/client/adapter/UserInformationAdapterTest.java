package com.pragma.plazoleta.infrastructure.client.adapter;

import com.pragma.plazoleta.infrastructure.client.dto.RoleResponseDto;
import com.pragma.plazoleta.infrastructure.client.dto.UserResponseDto;
import com.pragma.plazoleta.infrastructure.client.feign.UserFeignClient;
import com.pragma.plazoleta.infrastructure.security.JwtAdapter;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInformationAdapterTest {

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private JwtAdapter jwtAdapter;

    @InjectMocks
    private UserInformationAdapter userInformationAdapter;

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    // --- Pruebas de isValidOwner (Feign Client) ---

    @Test
    @DisplayName("Should return true when user has owner role via roleId")
    void shouldReturnTrueWhenUserHasOwnerRoleId() {
        UserResponseDto user = new UserResponseDto();
        user.setId(2L);
        user.setRoleId(2L);

        when(userFeignClient.getUserById(2L)).thenReturn(user);

        boolean result = userInformationAdapter.isValidOwner(2L);

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

        boolean result = userInformationAdapter.isValidOwner(2L);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when user has different role")
    void shouldReturnFalseWhenUserHasDifferentRole() {
        UserResponseDto user = new UserResponseDto();
        user.setId(3L);
        user.setRoleId(1L);

        when(userFeignClient.getUserById(3L)).thenReturn(user);

        boolean result = userInformationAdapter.isValidOwner(3L);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when feign client returns null")
    void shouldReturnFalseWhenUserIsNull() {
        when(userFeignClient.getUserById(4L)).thenReturn(null);

        boolean result = userInformationAdapter.isValidOwner(4L);

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

        boolean result = userInformationAdapter.isValidOwner(99L);

        assertFalse(result);
    }

    // --- Pruebas de getAuthenticatedUserId (RequestContext & JWT) ---

    @Test
    @DisplayName("Should successfully extract authenticated user ID from active request context")
    void shouldExtractAuthenticatedUserIdSuccessfully() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(jwtAdapter.validateToken("valid.token")).thenReturn(true);
        when(jwtAdapter.extractUserId("valid.token")).thenReturn(2L);

        Long userId = userInformationAdapter.getAuthenticatedUserId();

        assertEquals(2L, userId);
        verify(jwtAdapter).validateToken("valid.token");
        verify(jwtAdapter).extractUserId("valid.token");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when no HTTP request context is available")
    void shouldThrowWhenNoRequestContextAvailable() {
        RequestContextHolder.resetRequestAttributes();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userInformationAdapter.getAuthenticatedUserId()
        );
        assertEquals("No hay una petición HTTP activa en el contexto de ejecución", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when Authorization header is missing")
    void shouldThrowWhenAuthorizationHeaderIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userInformationAdapter.getAuthenticatedUserId()
        );
        assertEquals("Encabezado de autorización ausente o con formato inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when Authorization header does not start with Bearer")
    void shouldThrowWhenAuthorizationHeaderIsNotBearer() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic user:pass");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userInformationAdapter.getAuthenticatedUserId()
        );
        assertEquals("Encabezado de autorización ausente o con formato inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when token is invalid or expired")
    void shouldThrowWhenTokenIsInvalidOrExpired() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid.token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(jwtAdapter.validateToken("invalid.token")).thenReturn(false);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userInformationAdapter.getAuthenticatedUserId()
        );
        assertEquals("El token JWT proporcionado es inválido o ha expirado", exception.getMessage());
        verify(jwtAdapter, never()).extractUserId(any());
    }
}