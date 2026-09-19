package com.spendsmart.category.controller;

import com.spendsmart.category.dto.CategoryResponse;
import com.spendsmart.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Admin Categories", description = "Admin-only category management endpoints")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories (admin)")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID (admin)")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryByIdAdmin(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search categories by keyword (admin)")
    public ResponseEntity<List<CategoryResponse>> searchCategories(@RequestParam String keyword) {
        return ResponseEntity.ok(categoryService.searchCategories(keyword));
    }

    @GetMapping("/count")
    @Operation(summary = "Get total category count (admin)")
    public ResponseEntity<Map<String, Long>> getTotalCount() {
        return ResponseEntity.ok(Map.of("count", categoryService.getTotalCategoryCount()));
    }
}
