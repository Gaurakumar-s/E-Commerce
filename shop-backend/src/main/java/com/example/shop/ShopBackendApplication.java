package com.example.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ShopBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopBackendApplication.class, args);
    }
}

