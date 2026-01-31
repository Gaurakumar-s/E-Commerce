package com.example.shop.dto;

import com.example.shop.model.OrderStatus;
import com.example.shop.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String paymentReference;
    private Instant createdAt;
    private List<OrderItemResponse> items;
}

