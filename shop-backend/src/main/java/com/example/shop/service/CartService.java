package com.example.shop.service;

import com.example.shop.dto.AddCartItemRequest;
import com.example.shop.dto.CartItemResponse;
import com.example.shop.dto.CartResponse;
import com.example.shop.dto.UpdateCartItemRequest;
import com.example.shop.model.Cart;
import com.example.shop.model.CartItem;
import com.example.shop.model.Product;
import com.example.shop.model.User;
import com.example.shop.repository.CartItemRepository;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse getOrCreateCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });

        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String userEmail, AddCartItemRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.isActive() || product.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("Product is not available");
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst();

        int newQuantity;
        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            newQuantity = item.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStockQuantity()) {
                throw new IllegalArgumentException(
                        "Requested quantity exceeds available stock. Available: " + product.getStockQuantity()
                );
            }
            item.setQuantity(newQuantity);
        } else {
            if (request.getQuantity() > product.getStockQuantity()) {
                throw new IllegalArgumentException(
                        "Requested quantity exceeds available stock. Available: " + product.getStockQuantity()
                );
            }
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .priceAtAddTime(product.getPrice())
                    .build();
            cart.getItems().add(item);
        }

        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    @Transactional
    public CartResponse updateItem(String userEmail, Long itemId, UpdateCartItemRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        Product product = item.getProduct();
        if (request.getQuantity() > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock. Available: " + product.getStockQuantity()
            );
        }

        item.setQuantity(request.getQuantity());
        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    @Transactional
    public CartResponse removeItem(String userEmail, Long itemId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        boolean removed = cart.getItems().removeIf(i -> i.getId().equals(itemId));
        if (!removed) {
            throw new IllegalArgumentException("Cart item not found");
        }

        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    @Transactional
    public CartResponse clearCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cart.getItems().clear();
        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    public CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalAmount(total)
                .lastUpdated(cart.getLastUpdated())
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        BigDecimal lineTotal = item.getPriceAtAddTime()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .priceAtAddTime(item.getPriceAtAddTime())
                .lineTotal(lineTotal)
                .build();
    }
}

