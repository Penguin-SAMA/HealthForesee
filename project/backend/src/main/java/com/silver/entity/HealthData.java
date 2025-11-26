package com.silver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "health_data")
public class HealthData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 用户ID
    private Long userId;
    
    // 心率
    private Integer heartRate;
    
    // 血糖
    private Double bloodSugar;
    
    // 测量时间
    private LocalDateTime measureTime = LocalDateTime.now();
    
    // 健康状态
    private String healthStatus;
}