package com.pragma.plazoleta.domain.model;

public class UserInfo {

    private final Long id;
    private final String email;
    private final String phoneNumber;

    public UserInfo(Long id, String email, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
