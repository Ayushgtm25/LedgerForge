package com.spendsmart.category.service;

import com.spendsmart.category.dto.CategoryRequest;
import com.spendsmart.category.dto.CategoryResponse;
import com.spendsmart.category.entity.Category;
import com.spendsmart.category.entity.Category.CategoryType;
import com.spendsmart.category.exception.DuplicateCategoryException;
import com.spendsmart.category.exception.ResourceNotFoundException;
import com.spendsmart.category.exception.SubscriptionLimitExceededException;
import com.spendsmart.category.exception.UnauthorizedAccessException;
import com.spendsmart.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private static final Long USER_ID = 1L;
    private static final Long CATEGORY_ID = 10L;
    private static final String NORMAL = "NORMAL";
    private static final String PAID = "PAID";

    private Category sampleCategory;
    private CategoryRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleCategory = Category.builder()
                .categoryId(CATEGORY_ID)
                .userId(USER_ID)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .icon("🍔")
                .colorCode("#0891B2")
                .isDefault(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = CategoryRequest.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .icon("🍔")
                .colorCode("#0891B2")
                .build();
    }

    @Nested
    @DisplayName("Create Category")
    class CreateCategory {

        @Test
        @DisplayName("Should create category successfully")
        void shouldCreateCategory() {
            when(categoryRepository.existsByUserIdAndNameAndType(USER_ID, "Food", CategoryType.EXPENSE))
                    .thenReturn(false);
            when(categoryRepository.countByUserIdAndTypeAndIsDefaultFalse(USER_ID, CategoryType.EXPENSE))
                    .thenReturn(2L);
            when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

            CategoryResponse result = categoryService.createCategory(USER_ID, NORMAL, sampleRequest);

            assertNotNull(result);
            assertEquals("Food", result.getName());
            assertEquals("EXPENSE", result.getType());
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Should reject duplicate category")
        void shouldRejectDuplicate() {
            when(categoryRepository.existsByUserIdAndNameAndType(USER_ID, "Food", CategoryType.EXPENSE))
                    .thenReturn(true);

            assertThrows(DuplicateCategoryException.class,
                    () -> categoryService.createCategory(USER_ID, NORMAL, sampleRequest));
            verify(categoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reject custom category creation for NORMAL users at limit")
        void shouldRejectWhenNormalUserAtLimit() {
            when(categoryRepository.existsByUserIdAndNameAndType(USER_ID, "Food", CategoryType.EXPENSE))
                    .thenReturn(false);
            when(categoryRepository.countByUserIdAndTypeAndIsDefaultFalse(USER_ID, CategoryType.EXPENSE))
                    .thenReturn(5L);

            assertThrows(SubscriptionLimitExceededException.class,
                    () -> categoryService.createCategory(USER_ID, NORMAL, sampleRequest));
            verify(categoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow custom category creation for PAID users")
        void shouldAllowPaidUserBeyondNormalLimit() {
            when(categoryRepository.existsByUserIdAndNameAndType(USER_ID, "Food", CategoryType.EXPENSE))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

            CategoryResponse result = categoryService.createCategory(USER_ID, PAID, sampleRequest);

            assertNotNull(result);
            verify(categoryRepository, never()).countByUserIdAndTypeAndIsDefaultFalse(anyLong(), any());
            verify(categoryRepository).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Update Category")
    class UpdateCategory {

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategory() {
            when(categoryRepository.findByCategoryIdAndUserId(CATEGORY_ID, USER_ID))
                    .thenReturn(Optional.of(sampleCategory));
            when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

            CategoryRequest updateRequest = CategoryRequest.builder()
                    .name("Food")
                    .type(CategoryType.EXPENSE)
                    .icon("🍕")
                    .colorCode("#FF5733")
                    .build();

            CategoryResponse result = categoryService.updateCategory(USER_ID, CATEGORY_ID, updateRequest);

            assertNotNull(result);
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Should reject update for non-existent category")
        void shouldRejectUpdateForNonExistent() {
            when(categoryRepository.findByCategoryIdAndUserId(CATEGORY_ID, USER_ID))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> categoryService.updateCategory(USER_ID, CATEGORY_ID, sampleRequest));
        }

        @Test
        @DisplayName("Should reject update with duplicate name")
        void shouldRejectUpdateWithDuplicateName() {
            CategoryRequest renameRequest = CategoryRequest.builder()
                    .name("Transport")
                    .type(CategoryType.EXPENSE)
                    .build();

            when(categoryRepository.findByCategoryIdAndUserId(CATEGORY_ID, USER_ID))
                    .thenReturn(Optional.of(sampleCategory));
            when(categoryRepository.existsByUserIdAndNameAndType(USER_ID, "Transport", CategoryType.EXPENSE))
                    .thenReturn(true);

            assertThrows(DuplicateCategoryException.class,
                    () -> categoryService.updateCategory(USER_ID, CATEGORY_ID, renameRequest));
        }
    }

    @Nested
    @DisplayName("Delete Category")
    class DeleteCategory {

        @Test
        @DisplayName("Should delete category successfully")
        void shouldDeleteCategory() {
            when(categoryRepository.findByCategoryIdAndUserId(CATEGORY_ID, USER_ID))
                    .thenReturn(Optional.of(sampleCategory));

            categoryService.deleteCategory(USER_ID, CATEGORY_ID);

            verify(categoryRepository).delete(sampleCategory);
        }

        @Test
        @DisplayName("Should reject delete for non-existent category")
        void shouldRejectDeleteForNonExistent() {
            when(categoryRepository.findByCategoryIdAndUserId(CATEGORY_ID, USER_ID))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> categoryService.deleteCategory(USER_ID, CATEGORY_ID));
        }
    }

    @Nested
    @DisplayName("Get Categories")
    class GetCategories {

        @Test
        @DisplayName("Should get categories by user")
        void shouldGetCategoriesByUser() {
            when(categoryRepository.findByUserId(USER_ID)).thenReturn(List.of(sampleCategory));

            List<CategoryResponse> result = categoryService.getCategoriesByUser(USER_ID);

            assertEquals(1, result.size());
            assertEquals("Food", result.get(0).getName());
        }

        @Test
        @DisplayName("Should get categories by type")
        void shouldGetCategoriesByType() {
            when(categoryRepository.findByUserIdAndType(USER_ID, CategoryType.EXPENSE))
                    .thenReturn(List.of(sampleCategory));

            List<CategoryResponse> result = categoryService.getCategoriesByType(USER_ID, CategoryType.EXPENSE);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should get category by ID")
        void shouldGetCategoryById() {
            when(categoryRepository.findByCategoryIdAndUserId(CATEGORY_ID, USER_ID))
                    .thenReturn(Optional.of(sampleCategory));

            CategoryResponse result = categoryService.getCategoryById(USER_ID, CATEGORY_ID, "USER");

            assertNotNull(result);
            assertEquals(CATEGORY_ID, result.getCategoryId());
        }
    }

    @Nested
    @DisplayName("Seed Default Categories")
    class SeedDefaults {

        @Test
        @DisplayName("Should seed default categories for new user")
        void shouldSeedDefaults() {
            when(categoryRepository.findByUserIdAndIsDefaultTrue(USER_ID))
                    .thenReturn(Collections.emptyList());
            when(categoryRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

            categoryService.seedDefaultCategories(USER_ID);

            verify(categoryRepository).saveAll(argThat(list -> ((java.util.Collection<?>) list).size() == 12));
        }

        @Test
        @DisplayName("Should skip seeding if defaults already exist")
        void shouldSkipSeedingIfDefaultsExist() {
            when(categoryRepository.findByUserIdAndIsDefaultTrue(USER_ID))
                    .thenReturn(List.of(sampleCategory));

            categoryService.seedDefaultCategories(USER_ID);

            verify(categoryRepository, never()).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("Budget Limit")
    class BudgetLimit {

        @Test
        @DisplayName("Should set budget limit")
        void shouldSetBudgetLimit() {
            BigDecimal limit = new BigDecimal("5000.00");
            when(categoryRepository.findByCategoryIdAndUserId(CATEGORY_ID, USER_ID))
                    .thenReturn(Optional.of(sampleCategory));
            when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

            CategoryResponse result = categoryService.setCategoryBudgetLimit(USER_ID, CATEGORY_ID, limit);

            assertNotNull(result);
            verify(categoryRepository).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Validate Category")
    class ValidateCategory {

        @Test
        @DisplayName("Should validate category for user")
        void shouldValidateCategory() {
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(sampleCategory));

            CategoryResponse result = categoryService.validateCategoryForUser(USER_ID, CATEGORY_ID, CategoryType.EXPENSE);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should reject validation for wrong user")
        void shouldRejectValidationForWrongUser() {
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(sampleCategory));

            assertThrows(UnauthorizedAccessException.class,
                    () -> categoryService.validateCategoryForUser(999L, CATEGORY_ID, CategoryType.EXPENSE));
        }

        @Test
        @DisplayName("Should reject validation for wrong type")
        void shouldRejectValidationForWrongType() {
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(sampleCategory));

            assertThrows(UnauthorizedAccessException.class,
                    () -> categoryService.validateCategoryForUser(USER_ID, CATEGORY_ID, CategoryType.INCOME));
        }

        @Test
        @DisplayName("Should reject validation for non-existent category")
        void shouldRejectValidationForNonExistent() {
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> categoryService.validateCategoryForUser(USER_ID, CATEGORY_ID, CategoryType.EXPENSE));
        }
    }

    @Nested
    @DisplayName("Category Count")
    class CategoryCount {

        @Test
        @DisplayName("Should get category count")
        void shouldGetCount() {
            when(categoryRepository.countByUserId(USER_ID)).thenReturn(6L);

            long count = categoryService.getCategoryCount(USER_ID);

            assertEquals(6L, count);
        }

        @Test
        @DisplayName("Should get category count by type")
        void shouldGetCountByType() {
            when(categoryRepository.countByUserIdAndType(USER_ID, CategoryType.EXPENSE)).thenReturn(3L);

            long count = categoryService.getCategoryCountByType(USER_ID, CategoryType.EXPENSE);

            assertEquals(3L, count);
        }
    }

    @Nested
    @DisplayName("Admin Operations")
    class AdminOperations {

        @Test
        @DisplayName("Should get all categories as admin")
        void shouldGetAllCategories() {
            when(categoryRepository.findAll()).thenReturn(List.of(sampleCategory));

            List<CategoryResponse> result = categoryService.getAllCategories();

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should get category by ID as admin")
        void shouldGetCategoryByIdAdmin() {
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(sampleCategory));

            CategoryResponse result = categoryService.getCategoryByIdAdmin(CATEGORY_ID);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should get total category count")
        void shouldGetTotalCount() {
            when(categoryRepository.count()).thenReturn(100L);

            long count = categoryService.getTotalCategoryCount();

            assertEquals(100L, count);
        }
    }
}
