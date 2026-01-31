package com.example.shop.service;

import com.example.shop.dto.CategoryRequest;
import com.example.shop.dto.CategoryResponse;
import com.example.shop.model.Category;
import com.example.shop.repository.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @CacheEvict(value = {"categories"}, allEntries = true)
    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParentCategory(parent);
        }
        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = {"categories"}, allEntries = true)
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setName(request.getName());
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }
        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = {"categories"}, allEntries = true)
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    @Cacheable(value = "categories")
    @Transactional(readOnly = true)
    public List<CategoryResponse> listAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse toResponse(Category category) {
        Long parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentCategoryId(parentId)
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}

