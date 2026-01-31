package com.example.shop.service;

import com.example.shop.dto.RevenueResponse;
import com.example.shop.dto.TopProductResponse;
import com.example.shop.model.Order;
import com.example.shop.model.OrderStatus;
import com.example.shop.model.Product;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public AnalyticsService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<TopProductResponse> getTopProductsByRevenue(int limit, Instant startDate, Instant endDate) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> {
                    Instant createdAt = order.getCreatedAt();
                    return (startDate == null || !createdAt.isBefore(startDate)) &&
                           (endDate == null || !createdAt.isAfter(endDate)) &&
                           order.getPaymentStatus().name().equals("PAID");
                })
                .collect(Collectors.toList());

        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                items -> {
                                    if (items.isEmpty()) {
                                        return new Object[] {0L, BigDecimal.ZERO, ""};
                                    }
                                    long totalQty = items.stream()
                                            .mapToLong(com.example.shop.model.OrderItem::getQuantity)
                                            .sum();
                                    BigDecimal totalRev = items.stream()
                                            .map(com.example.shop.model.OrderItem::getSubtotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    String productName = items.get(0).getProduct().getName();
                                    return new Object[] {totalQty, totalRev, productName};
                                }
                        )
                ))
                .entrySet().stream()
                .map(entry -> {
                    Object[] data = entry.getValue();
                    if (data == null || data.length < 3) {
                        return TopProductResponse.builder()
                                .productId(entry.getKey())
                                .productName("")
                                .totalQuantitySold(0L)
                                .totalRevenue(BigDecimal.ZERO)
                                .build();
                    }
                    return TopProductResponse.builder()
                            .productId(entry.getKey())
                            .productName(data[2] != null ? (String) data[2] : "")
                            .totalQuantitySold(data[0] != null ? (Long) data[0] : 0L)
                            .totalRevenue(data[1] != null ? (BigDecimal) data[1] : BigDecimal.ZERO)
                            .build();
                })
                .sorted((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopProductResponse> getTopProductsByQuantity(int limit, Instant startDate, Instant endDate) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> {
                    Instant createdAt = order.getCreatedAt();
                    return (startDate == null || !createdAt.isBefore(startDate)) &&
                           (endDate == null || !createdAt.isAfter(endDate)) &&
                           order.getPaymentStatus().name().equals("PAID");
                })
                .collect(Collectors.toList());

        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getId(),
                        Collectors.reducing(
                                0L,
                                item -> (long) item.getQuantity(),
                                Long::sum
                        )
                ))
                .entrySet().stream()
                .map(entry -> {
                    Product product = productRepository.findById(entry.getKey())
                            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                    return TopProductResponse.builder()
                            .productId(entry.getKey())
                            .productName(product.getName())
                            .totalQuantitySold(entry.getValue())
                            .totalRevenue(BigDecimal.ZERO)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getTotalQuantitySold(), a.getTotalQuantitySold()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RevenueResponse> getDailyRevenue(Instant startDate, Instant endDate) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> {
                    Instant createdAt = order.getCreatedAt();
                    return (startDate == null || !createdAt.isBefore(startDate)) &&
                           (endDate == null || !createdAt.isAfter(endDate)) &&
                           order.getPaymentStatus().name().equals("PAID");
                })
                .collect(Collectors.toList());

        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> LocalDate.ofInstant(order.getCreatedAt(), ZoneId.systemDefault()),
                        Collectors.reducing(
                                new Object[] {BigDecimal.ZERO, 0L},
                                order -> new Object[] {order.getTotalAmount(), 1L},
                                (a, b) -> new Object[] {
                                        ((BigDecimal) a[0]).add((BigDecimal) b[0]),
                                        (Long) a[1] + (Long) b[1]
                                }
                        )
                ))
                .entrySet().stream()
                .map(entry -> {
                    Object[] data = entry.getValue();
                    if (data == null || data.length < 2) {
                        return RevenueResponse.builder()
                                .date(entry.getKey().atStartOfDay(ZoneId.systemDefault()).toInstant())
                                .totalRevenue(BigDecimal.ZERO)
                                .orderCount(0L)
                                .build();
                    }
                    return RevenueResponse.builder()
                            .date(entry.getKey().atStartOfDay(ZoneId.systemDefault()).toInstant())
                            .totalRevenue(data[0] != null ? (BigDecimal) data[0] : BigDecimal.ZERO)
                            .orderCount(data[1] != null ? (Long) data[1] : 0L)
                            .build();
                })
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue(Instant startDate, Instant endDate) {
        return orderRepository.findAll().stream()
                .filter(order -> {
                    Instant createdAt = order.getCreatedAt();
                    return (startDate == null || !createdAt.isBefore(startDate)) &&
                           (endDate == null || !createdAt.isAfter(endDate)) &&
                           order.getPaymentStatus().name().equals("PAID");
                })
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public Long getTotalOrders(Instant startDate, Instant endDate) {
        return orderRepository.findAll().stream()
                .filter(order -> {
                    Instant createdAt = order.getCreatedAt();
                    return (startDate == null || !createdAt.isBefore(startDate)) &&
                           (endDate == null || !createdAt.isAfter(endDate));
                })
                .count();
    }

    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findAll().stream()
                .filter(product -> product.getStockQuantity() <= threshold && product.isActive())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getActiveUsersCount(Instant startDate, Instant endDate) {
        return orderRepository.findAll().stream()
                .filter(order -> {
                    Instant createdAt = order.getCreatedAt();
                    return (startDate == null || !createdAt.isBefore(startDate)) &&
                           (endDate == null || !createdAt.isAfter(endDate));
                })
                .map(order -> order.getUser().getId())
                .distinct()
                .count();
    }
}
