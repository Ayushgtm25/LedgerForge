package com.spendsmart.analytics.repository;

import com.spendsmart.analytics.entity.AnalyticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshot, Long> {

    List<AnalyticsSnapshot> findByUserIdOrderBySnapshotDateDesc(Long userId);

    List<AnalyticsSnapshot> findByUserIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(
            Long userId, LocalDate start, LocalDate end);

    Optional<AnalyticsSnapshot> findByUserIdAndSnapshotDate(Long userId, LocalDate snapshotDate);

    Optional<AnalyticsSnapshot> findTopByUserIdOrderBySnapshotDateDesc(Long userId);

    long countByUserId(Long userId);
}
