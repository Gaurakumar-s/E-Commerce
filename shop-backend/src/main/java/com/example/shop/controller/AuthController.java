package com.example.shop.controller;

import com.example.shop.dto.AuthResponse;
import com.example.shop.dto.LoginRequest;
import com.example.shop.dto.RegisterRequest;
import com.example.shop.dto.UserDto;
import com.example.shop.model.User;
import com.example.shop.repository.UserRepository;
import com.example.shop.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *   Ratnesh
 */
