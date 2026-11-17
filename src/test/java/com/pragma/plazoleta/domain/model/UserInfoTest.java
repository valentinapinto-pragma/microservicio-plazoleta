package com.pragma.plazoleta.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserInfoTest {

    @Test
    @DisplayName("Should create UserInfo instance and return getters correctly")
    void shouldCreateUserInfoInstance() {
        UserInfo userInfo = new UserInfo(1L, "cliente@test.com", "+573001234567");

        assertNotNull(userInfo);
        assertEquals(1L, userInfo.getId());
        assertEquals("cliente@test.com", userInfo.getEmail());
        assertEquals("+573001234567", userInfo.getPhoneNumber());
    }
}
