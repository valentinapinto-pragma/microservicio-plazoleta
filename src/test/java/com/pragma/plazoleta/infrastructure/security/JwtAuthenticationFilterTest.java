package com.pragma.plazoleta.infrastructure.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtAdapter jwtAdapter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Debe continuar la cadena de filtros sin autenticar cuando no hay header Authorization")
    void doFilterInternal_WhenNoAuthHeader_ShouldContinueChainWithoutAuthentication() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtAdapter);
    }

    @Test
    @DisplayName("Debe continuar la cadena sin autenticar cuando el header no inicia con Bearer")
    void doFilterInternal_WhenAuthHeaderNotBearer_ShouldContinueChainWithoutAuthentication() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcjpwYXNz");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtAdapter);
    }

    @Test
    @DisplayName("Debe continuar la cadena sin autenticar cuando el token JWT es inválido")
    void doFilterInternal_WhenTokenIsInvalid_ShouldContinueChainWithoutAuthentication() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer tokenInvalido");
        when(jwtAdapter.validateToken("tokenInvalido")).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Debe autenticar con prefijo ROLE_ cuando el rol del token no lo incluye")
    void doFilterInternal_WhenValidTokenWithoutRolePrefix_ShouldAuthenticateSuccessfully() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer tokenValido");
        when(jwtAdapter.validateToken("tokenValido")).thenReturn(true);
        when(jwtAdapter.extractUsername("tokenValido")).thenReturn("valentina@correo.com");
        when(jwtAdapter.extractRole("tokenValido")).thenReturn("OWNER");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("valentina@correo.com", authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OWNER")));
    }

    @Test
    @DisplayName("Debe autenticar respetando el prefijo ROLE_ si el token ya lo tiene")
    void doFilterInternal_WhenValidTokenWithRolePrefix_ShouldPreserveRolePrefix() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer tokenValido");
        when(jwtAdapter.validateToken("tokenValido")).thenReturn(true);
        when(jwtAdapter.extractUsername("tokenValido")).thenReturn("admin@correo.com");
        when(jwtAdapter.extractRole("tokenValido")).thenReturn("ROLE_ADMIN");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("admin@correo.com", authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("No debe autenticar si el rol es nulo")
    void doFilterInternal_WhenRoleIsNull_ShouldNotAuthenticate() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer tokenValido");
        when(jwtAdapter.validateToken("tokenValido")).thenReturn(true);
        when(jwtAdapter.extractRole("tokenValido")).thenReturn(null);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("No debe sobrescribir la autenticación si el contexto ya tiene un usuario autenticado")
    void doFilterInternal_WhenAlreadyAuthenticated_ShouldNotOverrideAuthentication() throws ServletException, IOException {
        UsernamePasswordAuthenticationToken existingAuth =
                new UsernamePasswordAuthenticationToken("usuarioPrevio", null);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer tokenValido");
        when(jwtAdapter.validateToken("tokenValido")).thenReturn(true);
        when(jwtAdapter.extractUsername("tokenValido")).thenReturn("nuevoUsuario@correo.com");
        when(jwtAdapter.extractRole("tokenValido")).thenReturn("OWNER");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals("usuarioPrevio", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}