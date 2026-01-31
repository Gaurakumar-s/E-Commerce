package com.example.shop.dto;

import com.example.shop.model.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private Set<Role> roles;
    private Instant createdAt;
    private Instant updatedAt;
}

