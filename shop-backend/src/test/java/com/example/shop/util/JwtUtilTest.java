package com.example.shop.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("test-secret-key-for-testing-purposes-only-minimum-32-characters", 3600000);
    }

    @Test
    void testGenerateToken() {
        // When
        String token = jwtUtil.generateToken("test@example.com");

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        // Given
        String token = jwtUtil.generateToken("test@example.com");

        // When
        String username = jwtUtil.extractUsername(token);

        // Then
        assertEquals("test@example.com", username);
    }

    @Test
    void testIsTokenValid_ValidToken() {
        // Given
        String token = jwtUtil.generateToken("test@example.com");

        // When
        boolean isValid = jwtUtil.isTokenValid(token, "test@example.com");

        // Then
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_InvalidUsername() {
        // Given
        String token = jwtUtil.generateToken("test@example.com");

        // When
        boolean isValid = jwtUtil.isTokenValid(token, "different@example.com");

        // Then
        assertFalse(isValid);
    }
}
