package com.example.shop.repository;

import com.example.shop.model.Cart;
import com.example.shop.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Cart> findByUser(User user);
}

