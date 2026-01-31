package com.example.shop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private Long parentCategoryId;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}

