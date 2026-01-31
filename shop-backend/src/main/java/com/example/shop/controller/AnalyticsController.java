package com.example.shop.controller;

import com.example.shop.dto.ProductResponse;
import com.example.shop.dto.RevenueResponse;
import com.example.shop.dto.TopProductResponse;
import com.example.shop.model.Product;
import com.example.shop.service.AnalyticsService;
import com.example.shop.service.ProductService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ProductService productService;

    public AnalyticsController(AnalyticsService analyticsService, ProductService productService) {
        this.analyticsService = analyticsService;
        this.productService = productService;
    }

    @GetMapping("/top-products/revenue")
    public ResponseEntity<List<TopProductResponse>> getTopProductsByRevenue(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
    ) {
        return ResponseEntity.ok(analyticsService.getTopProductsByRevenue(limit, startDate, endDate));
    }

    @GetMapping("/top-products/quantity")
    public ResponseEntity<List<TopProductResponse>> getTopProductsByQuantity(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
    ) {
        return ResponseEntity.ok(analyticsService.getTopProductsByQuantity(limit, startDate, endDate));
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<List<RevenueResponse>> getDailyRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
    ) {
        return ResponseEntity.ok(analyticsService.getDailyRevenue(startDate, endDate));
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
    ) {
        return ResponseEntity.ok(analyticsService.getTotalRevenue(startDate, endDate));
    }

    @GetMapping("/orders/total")
    public ResponseEntity<Long> getTotalOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
    ) {
        return ResponseEntity.ok(analyticsService.getTotalOrders(startDate, endDate));
    }

    @GetMapping("/products/low-stock")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold
    ) {
        List<Product> products = analyticsService.getLowStockProducts(threshold);
        List<ProductResponse> responses = products.stream()
                .map(productService::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/users/active")
    public ResponseEntity<Long> getActiveUsersCount(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
    ) {
        return ResponseEntity.ok(analyticsService.getActiveUsersCount(startDate, endDate));
    }
}
