package com.silver.service;

import com.silver.entity.HealthData;
import com.silver.entity.User;
import com.silver.repository.HealthDataRepository;
import com.silver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HealthDataService {
    
    @Autowired
    private HealthDataRepository healthDataRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 根据用户ID获取其关联的家庭成员的健康数据
     * @param userId 用户ID
     * @return 家庭成员的健康数据列表
     */
    public List<HealthData> getFamilyHealthData(Long userId) {
        // 在实际应用中，这里应该查询数据库获取当前用户关联的家庭成员
        // 然后获取这些家庭成员的健康数据
        // 目前我们返回模拟数据
        return new ArrayList<>();
    }
    
    /**
     * 保存健康数据
     * @param healthData 健康数据
     * @return 保存后的健康数据
     */
    public HealthData saveHealthData(HealthData healthData) {
        return healthDataRepository.save(healthData);
    }
    
    /**
     * 根据用户ID获取最新健康数据
     * @param userId 用户ID
     * @return 最新的健康数据
     */
    public List<HealthData> getLatestHealthDataByUserId(Long userId) {
        return healthDataRepository.findTop10ByUserIdOrderByMeasureTimeDesc(userId);
    }
    
    /**
     * 根据家庭成员ID列表获取他们的健康数据
     * @param memberIds 家庭成员ID列表
     * @return 健康数据列表
     */
    public List<HealthData> getHealthDataByMemberIds(List<Long> memberIds) {
        // 获取每个家庭成员的最新健康数据
        return memberIds.stream()
                .flatMap(id -> healthDataRepository.findTop10ByUserIdOrderByMeasureTimeDesc(id).stream())
                .collect(Collectors.toList());
    }
}