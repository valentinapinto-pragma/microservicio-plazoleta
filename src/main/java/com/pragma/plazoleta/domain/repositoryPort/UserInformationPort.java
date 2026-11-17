package com.pragma.plazoleta.domain.repositoryPort;

import com.pragma.plazoleta.domain.model.UserInfo;

public interface UserInformationPort {
    boolean isValidOwner(Long ownerId);
    Long getAuthenticatedUserId();
    boolean isValidEmployee(Long employeeId);
    UserInfo getUserById(Long userId);
}