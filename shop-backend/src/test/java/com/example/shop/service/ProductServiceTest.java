package com.example.shop.service;

import com.example.shop.dto.ProductRequest;
import com.example.shop.dto.ProductResponse;
import com.example.shop.model.Category;
import com.example.shop.model.Product;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .active(true)
                .build();

        productRequest = new ProductRequest();
        productRequest.setName("Laptop");
        productRequest.setDescription("High-performance laptop");
        productRequest.setPrice(new BigDecimal("999.99"));
        productRequest.setStockQuantity(50);
        productRequest.setCategoryId(1L);
        productRequest.setActive(true);

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .stockQuantity(50)
                .category(category)
                .active(true)
                .build();
    }

    @Test
    void testCreateProduct_Success() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When
        ProductResponse result = productService.create(productRequest);

        // Then
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(new BigDecimal("999.99"), result.getPrice());
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testCreateProduct_CategoryNotFound() {
        // Given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            productService.create(productRequest);
        });

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testGetById_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When
        ProductResponse result = productService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetById_ProductNotFound() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            productService.getById(1L);
        });
    }

    @Test
    void testSearchProducts() {
        // Given
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.search(
                null, null, null, null, null, null, Pageable.unpaged()
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // When
        productService.delete(1L);

        // Then
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Given
        when(productRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            productService.delete(1L);
        });

        verify(productRepository, never()).deleteById(anyLong());
    }
}
