package com.example.shop.service;

import com.example.shop.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> nameOrDescriptionContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) ->
                minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> active(Boolean active) {
        return (root, query, cb) ->
                active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<Product> inStock(Boolean inStock) {
        return (root, query, cb) -> {
            if (inStock == null) {
                return null;
            }
            if (inStock) {
                return cb.greaterThan(root.get("stockQuantity"), 0);
            } else {
                return cb.lessThanOrEqualTo(root.get("stockQuantity"), 0);
            }
        };
    }
}



