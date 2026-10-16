package com.pragma.plazoleta.infrastructure.client.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientDtoTest {

    @Test
    @DisplayName("Should test UserResponseDto and RoleResponseDto getters, setters and constructors")
    void testDtos() {
        RoleResponseDto role = new RoleResponseDto(2L, "PROPIETARIO", "Rol de propietario");
        assertEquals(2L, role.getId());
        assertEquals("PROPIETARIO", role.getName());
        assertEquals("Rol de propietario", role.getDescription());

        RoleResponseDto roleEmpty = new RoleResponseDto();
        roleEmpty.setId(1L);
        roleEmpty.setName("ADMIN");
        roleEmpty.setDescription("Rol admin");
        assertEquals(1L, roleEmpty.getId());

        UserResponseDto user = new UserResponseDto(5L, "Juan", "Perez", "juan@correo.com", 2L, role);
        assertEquals(5L, user.getId());
        assertEquals("Juan", user.getName());
        assertEquals("Perez", user.getLastName());
        assertEquals("juan@correo.com", user.getEmail());
        assertEquals(2L, user.getRoleId());
        assertEquals(role, user.getRole());

        UserResponseDto userEmpty = new UserResponseDto();
        userEmpty.setId(6L);
        userEmpty.setName("Maria");
        userEmpty.setLastName("Gomez");
        userEmpty.setEmail("maria@correo.com");
        userEmpty.setRoleId(2L);
        userEmpty.setRole(role);
        assertEquals(6L, userEmpty.getId());
    }
}
