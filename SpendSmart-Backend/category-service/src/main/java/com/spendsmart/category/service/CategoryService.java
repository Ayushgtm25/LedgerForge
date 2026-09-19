package com.spendsmart.category.service;

import com.spendsmart.category.dto.CategoryRequest;
import com.spendsmart.category.dto.CategoryResponse;
import com.spendsmart.category.entity.Category.CategoryType;

import java.math.BigDecimal;
import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(Long userId, String subscriptionType, CategoryRequest request);

    CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest request);

    void deleteCategory(Long userId, Long categoryId);

    CategoryResponse getCategoryById(Long userId, Long categoryId, String role);

    List<CategoryResponse> getCategoriesByUser(Long userId);

    List<CategoryResponse> getCategoriesByType(Long userId, CategoryType type);

    List<CategoryResponse> getDefaultCategories(Long userId);

    void seedDefaultCategories(Long userId);

    CategoryResponse setCategoryBudgetLimit(Long userId, Long categoryId, BigDecimal budgetLimit);

    long getCategoryCount(Long userId);

    long getCategoryCountByType(Long userId, CategoryType type);

    CategoryResponse validateCategoryForUser(Long userId, Long categoryId, CategoryType expectedType);

    // Admin methods
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryByIdAdmin(Long categoryId);

    List<CategoryResponse> searchCategories(String keyword);

    long getTotalCategoryCount();
}
