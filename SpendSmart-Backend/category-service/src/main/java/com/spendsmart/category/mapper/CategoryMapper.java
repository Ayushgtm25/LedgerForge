package com.spendsmart.category.mapper;

import com.spendsmart.category.dto.CategoryResponse;
import com.spendsmart.category.entity.Category;

public final class CategoryMapper {

    private CategoryMapper() {
        // Utility class
    }

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .userId(category.getUserId())
                .name(category.getName())
                .type(category.getType().name())
                .icon(category.getIcon())
                .colorCode(category.getColorCode())
                .budgetLimit(category.getBudgetLimit())
                .isDefault(category.getIsDefault())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
