package com.example.shop.config;

import com.example.shop.model.Category;
import com.example.shop.model.Product;
import com.example.shop.model.Role;
import com.example.shop.model.User;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Create Admin User if not exists
            if (userRepository.findByEmail("admin@shop.com").isEmpty()) {
                User admin = User.builder()
                        .name("Admin User")
                        .email("admin@shop.com")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .roles(Set.of(Role.ADMIN))
                        .build();
                userRepository.save(admin);
                System.out.println("Admin user created: admin@shop.com / admin123");
            }

            // Create Demo Customer if not exists
            if (userRepository.findByEmail("customer@shop.com").isEmpty()) {
                User customer = User.builder()
                        .name("Demo Customer")
                        .email("customer@shop.com")
                        .passwordHash(passwordEncoder.encode("customer123"))
                        .roles(Set.of(Role.CUSTOMER))
                        .build();
                userRepository.save(customer);
                System.out.println("Demo customer created: customer@shop.com / customer123");
            }

            // Create Categories if not exists
            Category electronics = categoryRepository.findByNameIgnoreCase("Electronics")
                    .orElseGet(() -> {
                        Category cat = Category.builder()
                                .name("Electronics")
                                .active(true)
                                .build();
                        return categoryRepository.save(cat);
                    });

            Category clothing = categoryRepository.findByNameIgnoreCase("Clothing")
                    .orElseGet(() -> {
                        Category cat = Category.builder()
                                .name("Clothing")
                                .active(true)
                                .build();
                        return categoryRepository.save(cat);
                    });

            Category books = categoryRepository.findByNameIgnoreCase("Books")
                    .orElseGet(() -> {
                        Category cat = Category.builder()
                                .name("Books")
                                .active(true)
                                .build();
                        return categoryRepository.save(cat);
                    });

            // Create Sample Products if not exists
            if (productRepository.count() == 0) {
                Product laptop = Product.builder()
                        .name("Laptop Pro 15")
                        .description("High-performance laptop with 16GB RAM and 512GB SSD")
                        .price(new BigDecimal("1299.99"))
                        .stockQuantity(50)
                        .category(electronics)
                        .active(true)
                        .build();
                productRepository.save(laptop);

                Product smartphone = Product.builder()
                        .name("Smartphone X")
                        .description("Latest smartphone with 128GB storage")
                        .price(new BigDecimal("799.99"))
                        .stockQuantity(100)
                        .category(electronics)
                        .active(true)
                        .build();
                productRepository.save(smartphone);

                Product tshirt = Product.builder()
                        .name("Cotton T-Shirt")
                        .description("Comfortable 100% cotton t-shirt")
                        .price(new BigDecimal("19.99"))
                        .stockQuantity(200)
                        .category(clothing)
                        .active(true)
                        .build();
                productRepository.save(tshirt);

                Product jeans = Product.builder()
                        .name("Denim Jeans")
                        .description("Classic fit denim jeans")
                        .price(new BigDecimal("49.99"))
                        .stockQuantity(75)
                        .category(clothing)
                        .active(true)
                        .build();
                productRepository.save(jeans);

                Product novel = Product.builder()
                        .name("Programming Guide")
                        .description("Complete guide to Spring Boot development")
                        .price(new BigDecimal("39.99"))
                        .stockQuantity(30)
                        .category(books)
                        .active(true)
                        .build();
                productRepository.save(novel);

                System.out.println("Sample products created");
            }
        };
    }
}
