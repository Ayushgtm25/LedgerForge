package com.spendsmart.income.repository;

import com.spendsmart.income.entity.Income;
import com.spendsmart.income.entity.Income.IncomeSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserIdOrderByDateDesc(Long userId);

    Optional<Income> findByIncomeIdAndUserId(Long incomeId, Long userId);

    List<Income> findByUserIdAndSource(Long userId, IncomeSource source);

    List<Income> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Income> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate from, LocalDate to);

    List<Income> findByUserIdAndTitleContainingIgnoreCaseOrderByDateDesc(Long userId, String keyword);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.userId = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.userId = :userId AND i.source = :source")
    BigDecimal sumAmountByUserIdAndSource(@Param("userId") Long userId, @Param("source") IncomeSource source);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.userId = :userId AND i.date BETWEEN :from AND :to")
    BigDecimal sumAmountByUserIdAndDateBetween(@Param("userId") Long userId,
                                               @Param("from") LocalDate from,
                                               @Param("to") LocalDate to);

    long countByUserId(Long userId);
}
