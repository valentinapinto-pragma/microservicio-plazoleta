package com.pragma.plazoleta.domain.repositoryPort;

public interface UserInformationPort {
    boolean isValidOwner(Long ownerId);
    Long getAuthenticatedUserId();
}
