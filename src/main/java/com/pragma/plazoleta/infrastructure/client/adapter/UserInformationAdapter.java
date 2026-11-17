package com.pragma.plazoleta.infrastructure.client.adapter;

import com.pragma.plazoleta.domain.exception.UserNotAuthenticatedException;
import com.pragma.plazoleta.domain.model.RoleEnum;
import com.pragma.plazoleta.domain.model.UserInfo;
import com.pragma.plazoleta.domain.repositoryPort.UserInformationPort;
import com.pragma.plazoleta.infrastructure.client.dto.UserResponseDto;
import com.pragma.plazoleta.infrastructure.client.feign.UserFeignClient;
import com.pragma.plazoleta.infrastructure.security.JwtAdapter;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class UserInformationAdapter implements UserInformationPort {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String NO_REQUEST_CONTEXT_MESSAGE = "No active HTTP request to resolve the authenticated user";
    private static final String MISSING_TOKEN_MESSAGE = "Authorization header is missing or does not use the Bearer scheme";
    private static final String INVALID_TOKEN_MESSAGE = "The provided JWT token is invalid or has expired";

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

    @Override
    public Long getAuthenticatedUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new UserNotAuthenticatedException(NO_REQUEST_CONTEXT_MESSAGE);
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new UserNotAuthenticatedException(MISSING_TOKEN_MESSAGE);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!jwtAdapter.validateToken(token)) {
            throw new UserNotAuthenticatedException(INVALID_TOKEN_MESSAGE);
        }

        return jwtAdapter.extractUserId(token);
    }

    @Override
    public boolean isValidEmployee(Long employeeId) {
        try {
            UserResponseDto user = userFeignClient.getUserById(employeeId);
            return isEmployeeRole(user);
        } catch (FeignException.NotFound e) {
            return false;
        }
    }

    @Override
    public UserInfo getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        try {
            UserResponseDto user = userFeignClient.getUserById(userId);
            if (user == null) {
                return null;
            }
            return new UserInfo(user.getId(), user.getEmail(), user.getPhoneNumber());
        } catch (FeignException.NotFound e) {
            return null;
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

    private boolean isEmployeeRole(UserResponseDto user) {
        if (user == null) {
            return false;
        }
        if (user.getRoleId() != null) {
            return RoleEnum.EMPLOYEE.getId().equals(user.getRoleId());
        }
        return user.getRole() != null && RoleEnum.EMPLOYEE.getId().equals(user.getRole().getId());
    }
}