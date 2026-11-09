package com.pragma.plazoleta.application.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaginationResponseDtoTest {

    @Test
    @DisplayName("Should correctly construct and expose pagination fields via all args constructor and getters")
    void shouldCreatePaginationResponseDtoWithAllArgs() {
        List<String> content = List.of("A", "B");
        PaginationResponseDto<String> dto = new PaginationResponseDto<>(content, 0, 10, 2L, 1);

        assertNotNull(dto);
        assertEquals(content, dto.getContent());
        assertEquals(0, dto.getCurrentPage());
        assertEquals(10, dto.getPageSize());
        assertEquals(2L, dto.getTotalElements());
        assertEquals(1, dto.getTotalPages());
    }

    @Test
    @DisplayName("Should correctly set and get fields via setters and no args constructor")
    void shouldCreatePaginationResponseDtoWithNoArgsAndSetters() {
        List<String> content = List.of("X");
        PaginationResponseDto<String> dto = new PaginationResponseDto<>();
        dto.setContent(content);
        dto.setCurrentPage(1);
        dto.setPageSize(5);
        dto.setTotalElements(10L);
        dto.setTotalPages(2);

        assertNotNull(dto);
        assertEquals(content, dto.getContent());
        assertEquals(1, dto.getCurrentPage());
        assertEquals(5, dto.getPageSize());
        assertEquals(10L, dto.getTotalElements());
        assertEquals(2, dto.getTotalPages());
    }
}