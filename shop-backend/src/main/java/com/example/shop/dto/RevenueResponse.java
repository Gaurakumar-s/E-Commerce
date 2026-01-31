package com.example.shop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class RevenueResponse {

    private Instant date;
    private BigDecimal totalRevenue;
    private Long orderCount;
}
