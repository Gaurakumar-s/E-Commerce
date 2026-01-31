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

/**
 *PRINCE
 */
