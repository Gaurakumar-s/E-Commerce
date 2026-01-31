package com.example.shop.service;

import com.example.shop.dto.OrderItemResponse;
import com.example.shop.dto.OrderResponse;
import com.example.shop.dto.PlaceOrderRequest;
import com.example.shop.model.Cart;
import com.example.shop.model.CartItem;
import com.example.shop.model.Order;
import com.example.shop.model.OrderItem;
import com.example.shop.model.OrderStatus;
import com.example.shop.model.PaymentStatus;
import com.example.shop.model.Product;
import com.example.shop.model.User;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository,
                       CartRepository cartRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       EmailService emailService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public OrderResponse placeOrder(String userEmail, PlaceOrderRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new java.util.ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (!product.isActive() || product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException(
                        "Product " + product.getName() + " is not available in requested quantity"
                );
            }

            BigDecimal priceEach = product.getPrice();
            BigDecimal subtotal = priceEach.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(null)
                    .product(product)
                    .productNameSnapshot(product.getName())
                    .quantity(cartItem.getQuantity())
                    .priceEach(priceEach)
                    .subtotal(subtotal)
                    .build();
            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .user(user)
                .items(orderItems)
                .totalAmount(totalAmount)
                .status(OrderStatus.CREATED)
                .paymentStatus(PaymentStatus.PAID)
                .paymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        cart.getItems().clear();
        cartRepository.save(cart);

        Order saved = orderRepository.save(order);
        emailService.sendOrderConfirmationEmail(
                user.getEmail(),
                user.getName(),
                saved.getId(),
                saved.getTotalAmount().toString()
        );
        return toResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Transactional
    public OrderResponse cancelOrder(String userEmail, Long orderId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Order does not belong to user");
        }

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Order cannot be cancelled");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return orderRepository.findByUser(user, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(OrderStatus status, Instant startDate, Instant endDate, Pageable pageable) {
        if (status != null && startDate != null && endDate != null) {
            return orderRepository.findByStatusAndCreatedAtBetween(status, startDate, endDate, pageable)
                    .map(this::toResponse);
        } else if (startDate != null && endDate != null) {
            return orderRepository.findByCreatedAtBetween(startDate, endDate, pageable)
                    .map(this::toResponse);
        } else {
            return orderRepository.findAll(pageable)
                    .map(this::toResponse);
        }
    }

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentReference(order.getPaymentReference())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductNameSnapshot())
                .quantity(item.getQuantity())
                .priceEach(item.getPriceEach())
                .subtotal(item.getSubtotal())
                .build();
    }
}
