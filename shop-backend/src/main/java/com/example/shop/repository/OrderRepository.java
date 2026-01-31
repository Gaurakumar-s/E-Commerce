package com.example.shop.repository;

import com.example.shop.model.Order;
import com.example.shop.model.OrderStatus;
import com.example.shop.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Page<Order> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Page<Order> findByStatusAndCreatedAtBetween(
            OrderStatus status,
            Instant start,
            Instant end,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Page<Order> findByCreatedAtBetween(
            Instant start,
            Instant end,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    @Override
    java.util.Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    @Override
    java.util.List<Order> findAll();
}

