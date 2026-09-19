package com.spendsmart.category.repository;

import com.spendsmart.category.entity.Category;
import com.spendsmart.category.entity.Category.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    List<Category> findByUserIdAndType(Long userId, CategoryType type);

    List<Category> findByUserIdAndIsDefaultTrue(Long userId);

    Optional<Category> findByCategoryIdAndUserId(Long categoryId, Long userId);

    boolean existsByUserIdAndNameAndType(Long userId, String name, CategoryType type);

    long countByUserId(Long userId);

    long countByUserIdAndType(Long userId, CategoryType type);

    long countByUserIdAndTypeAndIsDefaultFalse(Long userId, CategoryType type);

    void deleteByUserId(Long userId);

    List<Category> findByUserIdAndNameContainingIgnoreCase(Long userId, String keyword);
}
