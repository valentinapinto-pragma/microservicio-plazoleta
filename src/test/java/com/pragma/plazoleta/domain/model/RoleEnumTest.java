package com.pragma.plazoleta.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoleEnumTest {

    @Test
    @DisplayName("Should test all RoleEnum IDs, values and valueOf")
    void shouldTestRoleEnumValuesAndIds() {
        assertEquals(1L, RoleEnum.ADMIN.getId());
        assertEquals(2L, RoleEnum.OWNER.getId());
        assertEquals(3L, RoleEnum.EMPLOYEE.getId());
        assertEquals(4L, RoleEnum.CLIENT.getId());

        assertEquals(RoleEnum.ADMIN, RoleEnum.valueOf("ADMIN"));
        assertNotNull(RoleEnum.values());
    }
}