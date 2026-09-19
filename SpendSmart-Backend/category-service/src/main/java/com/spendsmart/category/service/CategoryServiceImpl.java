package com.spendsmart.category.service;

import com.spendsmart.category.dto.CategoryRequest;
import com.spendsmart.category.dto.CategoryResponse;
import com.spendsmart.category.entity.Category;
import com.spendsmart.category.entity.Category.CategoryType;
import com.spendsmart.category.exception.DuplicateCategoryException;
import com.spendsmart.category.exception.ResourceNotFoundException;
import com.spendsmart.category.exception.SubscriptionLimitExceededException;
import com.spendsmart.category.exception.UnauthorizedAccessException;
import com.spendsmart.category.mapper.CategoryMapper;
import com.spendsmart.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String SUBSCRIPTION_PAID = "PAID";
    private static final long NORMAL_CUSTOM_CATEGORY_LIMIT_PER_TYPE = 5;

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(Long userId, String subscriptionType, CategoryRequest request) {
        log.info("Creating category '{}' of type {} for user {}", request.getName(), request.getType(), userId);

        if (categoryRepository.existsByUserIdAndNameAndType(userId, request.getName(), request.getType())) {
            throw new DuplicateCategoryException(
                    String.format("Category '%s' of type %s already exists", request.getName(), request.getType()));
        }

        enforceCustomCategoryLimit(userId, subscriptionType, request.getType());

        Category category = Category.builder()
                .userId(userId)
                .name(request.getName())
                .type(request.getType())
                .icon(request.getIcon())
                .colorCode(request.getColorCode())
                .budgetLimit(request.getBudgetLimit())
                .isDefault(false)
                .build();

        category = categoryRepository.save(category);
        log.info("Category created successfully: id={}, name='{}'", category.getCategoryId(), category.getName());
        return CategoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest request) {
        log.info("Updating category {} for user {}", categoryId, userId);

        Category category = findCategoryByIdAndUser(categoryId, userId);

        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByUserIdAndNameAndType(userId, request.getName(), request.getType())) {
            throw new DuplicateCategoryException(
                    String.format("Category '%s' of type %s already exists", request.getName(), request.getType()));
        }

        category.setName(request.getName());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setColorCode(request.getColorCode());
        category.setBudgetLimit(request.getBudgetLimit());

        category = categoryRepository.save(category);
        log.info("Category updated successfully: id={}", categoryId);
        return CategoryMapper.toResponse(category);
    }

    @Override
    public void deleteCategory(Long userId, Long categoryId) {
        log.info("Deleting category {} for user {}", categoryId, userId);

        Category category = findCategoryByIdAndUser(categoryId, userId);
        categoryRepository.delete(category);
        log.info("Category deleted successfully: id={}", categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long userId, Long categoryId, String role) {
        if (ROLE_ADMIN.equals(role)) {
            return getCategoryByIdAdmin(categoryId);
        }
        Category category = findCategoryByIdAndUser(categoryId, userId);
        return CategoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByUser(Long userId) {
        log.debug("Fetching all categories for user {}", userId);
        return categoryRepository.findByUserId(userId).stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByType(Long userId, CategoryType type) {
        log.debug("Fetching {} categories for user {}", type, userId);
        return categoryRepository.findByUserIdAndType(userId, type).stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getDefaultCategories(Long userId) {
        log.debug("Fetching default categories for user {}", userId);
        return categoryRepository.findByUserIdAndIsDefaultTrue(userId).stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    public void seedDefaultCategories(Long userId) {
        log.info("Seeding default categories for user {}", userId);

        List<Category> existingDefaults = categoryRepository.findByUserIdAndIsDefaultTrue(userId);
        if (!existingDefaults.isEmpty()) {
            log.info("Default categories already exist for user {}. Skipping seed.", userId);
            return;
        }

        List<Category> defaults = buildDefaultCategories(userId);
        categoryRepository.saveAll(defaults);
        log.info("Seeded {} default categories for user {}", defaults.size(), userId);
    }

    @Override
    public CategoryResponse setCategoryBudgetLimit(Long userId, Long categoryId, BigDecimal budgetLimit) {
        log.info("Setting budget limit {} for category {} (user {})", budgetLimit, categoryId, userId);

        Category category = findCategoryByIdAndUser(categoryId, userId);
        category.setBudgetLimit(budgetLimit);
        category = categoryRepository.save(category);

        log.info("Budget limit updated for category {}", categoryId);
        return CategoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public long getCategoryCount(Long userId) {
        return categoryRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getCategoryCountByType(Long userId, CategoryType type) {
        return categoryRepository.countByUserIdAndType(userId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse validateCategoryForUser(Long userId, Long categoryId, CategoryType expectedType) {
        log.debug("Validating category {} for user {} with type {}", categoryId, userId, expectedType);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        if (!category.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("Category does not belong to this user");
        }

        if (category.getType() != expectedType) {
            throw new UnauthorizedAccessException(
                    String.format("Category type mismatch. Expected %s but got %s", expectedType, category.getType()));
        }

        return CategoryMapper.toResponse(category);
    }

    // --- Admin Methods ---

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.info("Admin: Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByIdAdmin(Long categoryId) {
        log.info("Admin: Fetching category {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return CategoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategories(String keyword) {
        log.info("Admin: Searching categories with keyword '{}'", keyword);
        return categoryRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase()))
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalCategoryCount() {
        return categoryRepository.count();
    }

    // --- Helper Methods ---

    private Category findCategoryByIdAndUser(Long categoryId, Long userId) {
        return categoryRepository.findByCategoryIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Category %d not found for user %d", categoryId, userId)));
    }

    private void enforceCustomCategoryLimit(Long userId, String subscriptionType, CategoryType type) {
        if (SUBSCRIPTION_PAID.equalsIgnoreCase(subscriptionType)) {
            return;
        }

        long customCount = categoryRepository.countByUserIdAndTypeAndIsDefaultFalse(userId, type);
        if (customCount >= NORMAL_CUSTOM_CATEGORY_LIMIT_PER_TYPE) {
            throw new SubscriptionLimitExceededException(
                    String.format("Free plan allows up to %d custom %s categories. Upgrade to PAID for unlimited categories.",
                            NORMAL_CUSTOM_CATEGORY_LIMIT_PER_TYPE,
                            type.name().toLowerCase())
            );
        }
    }

    private List<Category> buildDefaultCategories(Long userId) {
        return List.of(
                // Default expense categories
                buildDefault(userId, "Food", CategoryType.EXPENSE, "🍔", "#0891B2"),
                buildDefault(userId, "Transport", CategoryType.EXPENSE, "🚗", "#06B6D4"),
                buildDefault(userId, "Shopping", CategoryType.EXPENSE, "🛍️", "#22D3EE"),
                buildDefault(userId, "Bills", CategoryType.EXPENSE, "📄", "#67E8F9"),
                buildDefault(userId, "Health", CategoryType.EXPENSE, "🏥", "#A5F3FC"),
                buildDefault(userId, "Entertainment", CategoryType.EXPENSE, "🎬", "#0E7490"),
                // Default income categories
                buildDefault(userId, "Salary", CategoryType.INCOME, "💰", "#10B981"),
                buildDefault(userId, "Freelance", CategoryType.INCOME, "💻", "#34D399"),
                buildDefault(userId, "Business", CategoryType.INCOME, "🏢", "#6EE7B7"),
                buildDefault(userId, "Investment", CategoryType.INCOME, "📈", "#A7F3D0"),
                buildDefault(userId, "Gift", CategoryType.INCOME, "🎁", "#D1FAE5"),
                buildDefault(userId, "Other", CategoryType.INCOME, "📦", "#ECFDF5")
        );
    }

    private Category buildDefault(Long userId, String name, CategoryType type, String icon, String color) {
        return Category.builder()
                .userId(userId)
                .name(name)
                .type(type)
                .icon(icon)
                .colorCode(color)
                .isDefault(true)
                .build();
    }
}
