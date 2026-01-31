package com.example.shop.integration;

import com.example.shop.dto.PlaceOrderRequest;
import com.example.shop.model.*;
import com.example.shop.repository.*;
import com.example.shop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    private User user;
    private Category category;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        // Create user
        user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .roles(Set.of(Role.CUSTOMER))
                .build();
        user = userRepository.save(user);

        // Create category
        category = Category.builder()
                .name("Electronics")
                .active(true)
                .build();
        category = categoryRepository.save(category);

        // Create product
        product = Product.builder()
                .name("Laptop")
                .description("Test laptop")
                .price(new BigDecimal("999.99"))
                .stockQuantity(10)
                .category(category)
                .active(true)
                .build();
        product = productRepository.save(product);

        // Create cart with item
        cart = Cart.builder()
                .user(user)
                .build();
        cart = cartRepository.save(cart);

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .priceAtAddTime(product.getPrice())
                .build();
        cart.getItems().add(cartItem);
        cart = cartRepository.save(cart);
    }

    @Test
    void testPlaceOrder_Integration() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest();
        int initialStock = product.getStockQuantity();
        int orderQuantity = 2;

        // When
        var orderResponse = orderService.placeOrder("test@example.com", request);

        // Then
        assertNotNull(orderResponse);
        assertEquals(OrderStatus.CREATED, orderResponse.getStatus());
        assertEquals(PaymentStatus.PAID, orderResponse.getPaymentStatus());
        assertNotNull(orderResponse.getPaymentReference());
        assertEquals(1, orderResponse.getItems().size());

        // Verify stock was updated
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(initialStock - orderQuantity, updatedProduct.getStockQuantity());

        // Verify cart was cleared
        Cart updatedCart = cartRepository.findByUser(user).orElseThrow();
        assertTrue(updatedCart.getItems().isEmpty());

        // Verify order was saved
        assertTrue(orderRepository.existsById(orderResponse.getId()));
    }
}
