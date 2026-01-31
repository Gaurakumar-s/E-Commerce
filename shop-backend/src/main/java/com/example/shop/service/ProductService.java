package com.example.shop.service;

import com.example.shop.dto.ProductRequest;
import com.example.shop.dto.ProductResponse;
import com.example.shop.model.Category;
import com.example.shop.model.Product;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Cacheable(value = "productById", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return toResponse(product);
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public ProductResponse updateImage(Long id, String imageUrl) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setImageUrl(imageUrl);
        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Cacheable(value = "products")
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(
            Long categoryId,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean active,
            Boolean inStock,
            Pageable pageable
    ) {
        Specification<Product> spec = Specification.where(ProductSpecification.hasCategory(categoryId))
                .and(ProductSpecification.nameOrDescriptionContains(search))
                .and(ProductSpecification.priceGreaterThanOrEqual(minPrice))
                .and(ProductSpecification.priceLessThanOrEqual(maxPrice))
                .and(ProductSpecification.active(active))
                .and(ProductSpecification.inStock(inStock));

        return productRepository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    public ProductResponse toResponse(Product product) {
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(categoryId)
                .categoryName(categoryName)
                .imageUrl(product.getImageUrl())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}

