package com.silver.repository;

import com.silver.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Long> {
    List<HealthData> findByUserIdOrderByMeasureTimeDesc(Long userId);
    List<HealthData> findTop10ByUserIdOrderByMeasureTimeDesc(Long userId);
}