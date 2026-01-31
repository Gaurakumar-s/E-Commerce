package com.example.shop.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordEncoder() {
        // Given
        String rawPassword = "password123";

        // When
        String encoded = passwordEncoder.encode(rawPassword);

        // Then
        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);
        assertTrue(encoded.startsWith("$2a$")); // BCrypt prefix

        // Verify password matches
        assertTrue(passwordEncoder.matches(rawPassword, encoded));
        assertFalse(passwordEncoder.matches("wrongPassword", encoded));
    }
}
