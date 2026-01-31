package com.example.shop.service;

import com.example.shop.dto.PlaceOrderRequest;
import com.example.shop.model.*;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .roles(Set.of(Role.CUSTOMER))
                .build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .stockQuantity(10)
                .active(true)
                .build();

        cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .priceAtAddTime(new BigDecimal("999.99"))
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(Arrays.asList(cartItem))
                .build();
    }

    @Test
    void testPlaceOrder_Success() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        var result = orderService.placeOrder("test@example.com", request);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        assertNotNull(result.getPaymentReference());
        verify(orderRepository).save(any(Order.class));
        verify(productRepository, atLeastOnce()).save(any(Product.class));
        verify(cartRepository).save(any(Cart.class));
        verify(emailService).sendOrderConfirmationEmail(anyString(), anyString(), any(), anyString());
    }

    @Test
    void testPlaceOrder_CartEmpty() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest();
        Cart emptyCart = Cart.builder()
                .id(1L)
                .user(user)
                .items(Arrays.asList())
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(emptyCart));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder("test@example.com", request);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testPlaceOrder_InsufficientStock() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest();
        product.setStockQuantity(1); // Only 1 in stock, but cart has 2

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder("test@example.com", request);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testPlaceOrder_UserNotFound() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder("test@example.com", request);
        });
    }
}
