package com.example.shop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TopProductResponse {

    private Long productId;
    private String productName;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;
}
