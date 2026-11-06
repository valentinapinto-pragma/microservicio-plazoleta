package com.pragma.plazoleta.domain.model;

public enum RoleEnum {
    ADMIN(1L),
    OWNER(2L),
    EMPLOYEE(3L),
    CLIENT(4L);

    private final Long id;

    RoleEnum(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}