package com.example.shop.controller;

import com.example.shop.dto.ProductRequest;
import com.example.shop.dto.ProductResponse;
import com.example.shop.service.FileStorageService;
import com.example.shop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetProductById_Success() throws Exception {
        // Given
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .stockQuantity(50)
                .build();

        when(productService.getById(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    void testSearchProducts_Success() throws Exception {
        // Given
        ProductResponse product1 = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .build();

        ProductResponse product2 = ProductResponse.builder()
                .id(2L)
                .name("Phone")
                .price(new BigDecimal("599.99"))
                .build();

        Page<ProductResponse> page = new PageImpl<>(Arrays.asList(product1, product2));
        when(productService.search(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct_AdminOnly() throws Exception {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setPrice(new BigDecimal("99.99"));
        request.setStockQuantity(10);
        request.setCategoryId(1L);

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("New Product")
                .price(new BigDecimal("99.99"))
                .build();

        when(productService.create(any(ProductRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateProduct_ForbiddenForCustomer() throws Exception {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setPrice(new BigDecimal("99.99"));
        request.setStockQuantity(10);
        request.setCategoryId(1L);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
