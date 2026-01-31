package com.example.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderRequest {

    // For now we assume payment is always successful (mock).
    // This field can be used to simulate different payment methods.
    private String paymentMethod;
}

