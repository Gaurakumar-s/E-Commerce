package com.example.shop.controller;

import com.example.shop.dto.AuthResponse;
import com.example.shop.dto.LoginRequest;
import com.example.shop.dto.RegisterRequest;
import com.example.shop.dto.UserDto;
import com.example.shop.model.Role;
import com.example.shop.model.User;
import com.example.shop.repository.UserRepository;
import com.example.shop.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister_Success() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .roles(Set.of(Role.CUSTOMER))
                .build();

        when(authService.registerCustomer(any(RegisterRequest.class))).thenReturn(userDto);

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse response = AuthResponse.builder()
                .accessToken("jwt-token-here")
                .tokenType("Bearer")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token-here"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetMe_Success() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .roles(Set.of(Role.CUSTOMER))
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .roles(Set.of(Role.CUSTOMER))
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authService.toDto(any(User.class))).thenReturn(userDto);

        // When & Then
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }
}
