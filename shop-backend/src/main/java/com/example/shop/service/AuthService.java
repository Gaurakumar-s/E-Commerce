package com.example.shop.service;

import com.example.shop.dto.AuthResponse;
import com.example.shop.dto.LoginRequest;
import com.example.shop.dto.RegisterRequest;
import com.example.shop.dto.UserDto;
import com.example.shop.model.Role;
import com.example.shop.model.User;
import com.example.shop.repository.UserRepository;
import com.example.shop.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 *RATNESH
 */
