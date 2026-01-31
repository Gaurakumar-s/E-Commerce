package com.example.shop.controller;

import com.example.shop.dto.AddCartItemRequest;
import com.example.shop.dto.CartResponse;
import com.example.shop.dto.UpdateCartItemRequest;
import com.example.shop.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails principal) {
        CartResponse cart = cartService.getOrCreateCart(principal.getUsername());
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        CartResponse cart = cartService.addItem(principal.getUsername(), request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        CartResponse cart = cartService.updateItem(principal.getUsername(), itemId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long itemId
    ) {
        CartResponse cart = cartService.removeItem(principal.getUsername(), itemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart(@AuthenticationPrincipal UserDetails principal) {
        CartResponse cart = cartService.clearCart(principal.getUsername());
        return ResponseEntity.ok(cart);
    }
}

