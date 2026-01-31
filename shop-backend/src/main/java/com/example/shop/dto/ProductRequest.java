package com.example.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank
    @Size(min = 2, max = 200)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull
    @Min(0)
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    @NotNull
    private Long categoryId;

    private Boolean active;
}

