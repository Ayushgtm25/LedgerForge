package com.spendsmart.category.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_user_type", columnList = "user_id,type"),
        @Index(name = "idx_user_name_type", columnList = "user_id,name,type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryType type;

    @Column(length = 10)
    private String icon;

    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "budget_limit", precision = 19, scale = 2)
    private BigDecimal budgetLimit;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum CategoryType {
        EXPENSE,
        INCOME
    }
}
