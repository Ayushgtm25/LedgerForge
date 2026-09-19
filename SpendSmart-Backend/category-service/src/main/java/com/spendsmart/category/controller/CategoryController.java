package com.spendsmart.category.controller;

import com.spendsmart.category.dto.BudgetLimitRequest;
import com.spendsmart.category.dto.CategoryRequest;
import com.spendsmart.category.dto.CategoryResponse;
import com.spendsmart.category.entity.Category.CategoryType;
import com.spendsmart.category.security.JwtUserDetails;
import com.spendsmart.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<CategoryResponse> createCategory(
            @AuthenticationPrincipal JwtUserDetails user,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(user.getUserId(), user.getSubscriptionType(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(user.getUserId(), id, user.getRole());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all categories for the authenticated user")
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @AuthenticationPrincipal JwtUserDetails user) {
        List<CategoryResponse> categories = categoryService.getCategoriesByUser(user.getUserId());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get categories by type (EXPENSE or INCOME)")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByType(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable CategoryType type) {
        List<CategoryResponse> categories = categoryService.getCategoriesByType(user.getUserId(), type);
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category")
    public ResponseEntity<CategoryResponse> updateCategory(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(user.getUserId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<Void> deleteCategory(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        categoryService.deleteCategory(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/budget-limit")
    @Operation(summary = "Set budget limit for a category")
    public ResponseEntity<CategoryResponse> setBudgetLimit(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody BudgetLimitRequest request) {
        CategoryResponse response = categoryService.setCategoryBudgetLimit(user.getUserId(), id, request.getBudgetLimit());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    @Operation(summary = "Get category count for the authenticated user")
    public ResponseEntity<Map<String, Long>> getCategoryCount(
            @AuthenticationPrincipal JwtUserDetails user,
            @RequestParam(required = false) CategoryType type) {
        long count = type != null
                ? categoryService.getCategoryCountByType(user.getUserId(), type)
                : categoryService.getCategoryCount(user.getUserId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping("/seed-defaults")
    @Operation(summary = "Seed default categories for the authenticated user")
    public ResponseEntity<Map<String, String>> seedDefaults(
            @AuthenticationPrincipal JwtUserDetails user) {
        categoryService.seedDefaultCategories(user.getUserId());
        return ResponseEntity.ok(Map.of("message", "Default categories seeded successfully"));
    }

    @GetMapping("/defaults")
    @Operation(summary = "Get default categories for the authenticated user")
    public ResponseEntity<List<CategoryResponse>> getDefaults(
            @AuthenticationPrincipal JwtUserDetails user) {
        List<CategoryResponse> defaults = categoryService.getDefaultCategories(user.getUserId());
        return ResponseEntity.ok(defaults);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate a category belongs to the user and matches the expected type")
    public ResponseEntity<CategoryResponse> validateCategory(
            @AuthenticationPrincipal JwtUserDetails user,
            @RequestParam Long categoryId,
            @RequestParam CategoryType type) {
        CategoryResponse response = categoryService.validateCategoryForUser(user.getUserId(), categoryId, type);
        return ResponseEntity.ok(response);
    }
}
