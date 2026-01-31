package com.example.shop.repository;

import com.example.shop.model.Role;
import com.example.shop.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail_Success() {
        // Given
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .roles(Set.of(Role.CUSTOMER))
                .build();

        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    void testFindByEmail_NotFound() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByEmail_True() {
        // Given
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .roles(Set.of(Role.CUSTOMER))
                .build();

        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }
}
