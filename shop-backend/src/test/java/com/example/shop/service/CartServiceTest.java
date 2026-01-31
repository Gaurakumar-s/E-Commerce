package com.example.shop.service;

import com.example.shop.dto.AddCartItemRequest;
import com.example.shop.dto.CartResponse;
import com.example.shop.model.*;
import com.example.shop.repository.CartItemRepository;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;
    private Cart cart;
    private AddCartItemRequest addRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .roles(Set.of(Role.CUSTOMER))
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .stockQuantity(10)
                .category(category)
                .active(true)
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        addRequest = new AddCartItemRequest();
        addRequest.setProductId(1L);
        addRequest.setQuantity(2);
    }

    @Test
    void testGetOrCreateCart_ExistingCart() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // When
        CartResponse result = cartService.getOrCreateCart("test@example.com");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testGetOrCreateCart_NewCart() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // When
        CartResponse result = cartService.getOrCreateCart("test@example.com");

        // Then
        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testAddItem_Success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // When
        CartResponse result = cartService.addItem("test@example.com", addRequest);

        // Then
        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testAddItem_ProductNotFound() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addItem("test@example.com", addRequest);
        });
    }

    @Test
    void testAddItem_ProductNotAvailable() {
        // Given
        product.setActive(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addItem("test@example.com", addRequest);
        });
    }
}
