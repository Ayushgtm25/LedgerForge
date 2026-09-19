package com.spendsmart.category.dto;

import com.spendsmart.category.entity.Category.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Category type is required")
    private CategoryType type;

    @Size(max = 10, message = "Icon must not exceed 10 characters")
    private String icon;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color code must be a valid hex color (e.g., #FF5733)")
    private String colorCode;

    @PositiveOrZero(message = "Budget limit must be zero or positive")
    private BigDecimal budgetLimit;
}
