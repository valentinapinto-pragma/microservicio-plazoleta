package com.pragma.plazoleta.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtAdapterTest {

    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private JwtAdapter jwtAdapter;

    @BeforeEach
    void setUp() {
        jwtAdapter = new JwtAdapter(SECRET);
    }

    private String generateTestToken(Long id, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("role", role);

        Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject("owner@test.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    @Test
    @DisplayName("Should extract role and userId from valid token with Bearer prefix")
    void shouldExtractRoleAndUserIdFromBearerToken() {
        String rawToken = generateTestToken(2L, "ROLE_OWNER");
        String bearerToken = "Bearer " + rawToken;

        assertEquals("ROLE_OWNER", jwtAdapter.extractRole(bearerToken));
        assertEquals(2L, jwtAdapter.extractUserId(bearerToken));
    }

    @Test
    @DisplayName("Should return null when token is null or invalid")
    void shouldReturnNullWhenTokenIsInvalid() {
        assertNull(jwtAdapter.extractRole(null));
        assertNull(jwtAdapter.extractUserId(null));
        assertNull(jwtAdapter.extractRole("invalid.token.here"));
        assertNull(jwtAdapter.extractUserId("invalid.token.here"));
    }
}
