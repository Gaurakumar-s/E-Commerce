package com.example.shop.service;

import com.example.shop.dto.LoginRequest;
import com.example.shop.dto.RegisterRequest;
import com.example.shop.model.Role;
import com.example.shop.model.User;
import com.example.shop.repository.UserRepository;
import com.example.shop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        savedUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .roles(Set.of(Role.CUSTOMER))
                .build();
    }

    @Test
    void testRegisterCustomer_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        var result = authService.registerCustomer(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail(anyString(), anyString());
    }

    @Test
    void testRegisterCustomer_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerCustomer(registerRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken("test@example.com")).thenReturn("jwt-token");

        // When
        var result = authService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.getAccessToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("test@example.com");
    }
}
