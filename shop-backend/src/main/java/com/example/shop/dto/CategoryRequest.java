package com.example.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    private Long parentCategoryId;

    private Boolean active;
}

