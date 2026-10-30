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
        if (id != null) {
            claims.put("id", id);
        }
        if (role != null) {
            claims.put("role", role);
        }

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
    @DisplayName("Should extract role, userId, username and validate token with Bearer prefix")
    void shouldExtractDataFromBearerToken() {
        String rawToken = generateTestToken(2L, "ROLE_OWNER");
        String bearerToken = "Bearer " + rawToken;

        assertTrue(jwtAdapter.validateToken(bearerToken));
        assertEquals("ROLE_OWNER", jwtAdapter.extractRole(bearerToken));
        assertEquals(2L, jwtAdapter.extractUserId(bearerToken));
        assertEquals("owner@test.com", jwtAdapter.extractUsername(bearerToken));
    }

    @Test
    @DisplayName("Should extract role, userId, username and validate token without Bearer prefix")
    void shouldExtractDataFromRawTokenWithoutBearer() {
        String rawToken = generateTestToken(2L, "ROLE_OWNER");

        assertTrue(jwtAdapter.validateToken(rawToken));
        assertEquals("ROLE_OWNER", jwtAdapter.extractRole(rawToken));
        assertEquals(2L, jwtAdapter.extractUserId(rawToken));
        assertEquals("owner@test.com", jwtAdapter.extractUsername(rawToken));
    }

    @Test
    @DisplayName("Should return null or false when token is null or invalid")
    void shouldHandleNullOrInvalidToken() {
        assertFalse(jwtAdapter.validateToken(null));
        assertFalse(jwtAdapter.validateToken("invalid.token.here"));
        assertNull(jwtAdapter.extractRole(null));
        assertNull(jwtAdapter.extractUserId(null));
        assertNull(jwtAdapter.extractUsername(null));
        assertNull(jwtAdapter.extractRole("invalid.token.here"));
        assertNull(jwtAdapter.extractUserId("invalid.token.here"));
        assertNull(jwtAdapter.extractUsername("invalid.token.here"));
    }

    @Test
    @DisplayName("Should return null userId when token claims do not contain id")
    void shouldReturnNullUserIdWhenClaimsDoNotContainId() {
        String tokenWithoutId = generateTestToken(null, "ROLE_OWNER");

        assertTrue(jwtAdapter.validateToken(tokenWithoutId));
        assertNull(jwtAdapter.extractUserId(tokenWithoutId));
        assertEquals("ROLE_OWNER", jwtAdapter.extractRole(tokenWithoutId));
    }
}