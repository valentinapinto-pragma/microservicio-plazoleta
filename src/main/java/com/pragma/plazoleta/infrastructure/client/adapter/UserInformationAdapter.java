package com.pragma.plazoleta.infrastructure.client.adapter;

import com.pragma.plazoleta.domain.model.RoleEnum;
import com.pragma.plazoleta.domain.repositoryPort.UserInformationPort;
import com.pragma.plazoleta.infrastructure.client.dto.UserResponseDto;
import com.pragma.plazoleta.infrastructure.client.feign.UserFeignClient;
import com.pragma.plazoleta.infrastructure.security.JwtAdapter;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Component
@RequiredArgsConstructor
public class UserInformationAdapter implements UserInformationPort {

    private final UserFeignClient userFeignClient;
    private final JwtAdapter jwtAdapter;

    @Override
    public boolean isValidOwner(Long ownerId) {
        try {
            UserResponseDto user = userFeignClient.getUserById(ownerId);
            return isOwnerRole(user);
        } catch (FeignException.NotFound e) {
            return false;
        }
    }

    public Long getAuthenticatedUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new IllegalStateException("No hay una petición HTTP activa en el contexto de ejecución");
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("Encabezado de autorización ausente o con formato inválido");
        }

        String token = authHeader.substring(7);

        if (!jwtAdapter.validateToken(token)) {
            throw new IllegalStateException("El token JWT proporcionado es inválido o ha expirado");
        }

        return jwtAdapter.extractUserId(token);
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