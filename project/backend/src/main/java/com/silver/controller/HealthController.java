package com.silver.controller;

import com.silver.entity.FamilyRelation;
import com.silver.entity.HealthData;
import com.silver.entity.User;
import com.silver.service.HealthDataService;
import com.silver.service.UserService;
import com.silver.utils.Result;
import com.silver.utils.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
public class HealthController {
    
    @Autowired
    private HealthDataService healthDataService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/children")
    public Result<Map<String, Object>> getDashboard(@RequestHeader("Authorization") String token) {
        try {
            // 解析JWT Token获取当前用户信息
            Map<String, Object> userInfo = JwtUtil.parseToken(token.replace("Bearer ", ""));
            Long currentUserId = (Long) userInfo.get("userId");
            
            // 获取当前用户的家庭关系
            List<FamilyRelation> relations = userService.getFamilyRelationsByUserId(currentUserId);
            
            // 获取家庭成员信息和健康数据
            List<Map<String, Object>> parents = new ArrayList<>();
            for (FamilyRelation relation : relations) {
                Long memberId = relation.getMemberId();
                // 获取家庭成员信息
                User member = userService.findById(memberId).orElse(null);
                if (member != null) {
                    // 获取该成员的最新健康数据
                    List<HealthData> healthDataList = healthDataService.getLatestHealthDataByUserId(memberId);
                    HealthData latestHealthData = healthDataList.isEmpty() ? null : healthDataList.get(0);
                    
                    Map<String, Object> parentData = new HashMap<>();
                    parentData.put("userId", member.getId());
                    parentData.put("name", member.getName());
                    
                    // 根据健康数据设置健康状态
                    if (latestHealthData != null) {
                        parentData.put("healthStatus", latestHealthData.getHealthStatus() != null ? 
                                      latestHealthData.getHealthStatus() : "NORMAL");
                        Map<String, Object> metrics = new HashMap<>();
                        metrics.put("heartRate", latestHealthData.getHeartRate() != null ? 
                                   latestHealthData.getHeartRate() : 0);
                        metrics.put("bloodSugar", latestHealthData.getBloodSugar() != null ? 
                                   latestHealthData.getBloodSugar() : 0.0);
                        parentData.put("latestMetrics", metrics);
                    } else {
                        parentData.put("healthStatus", "NORMAL");
                        parentData.put("latestMetrics", Map.of("heartRate", 0, "bloodSugar", 0.0));
                    }
                    
                    parents.add(parentData);
                }
            }
            
            return Result.success(Map.of("parents", parents));
        } catch (Exception e) {
            return Result.error("获取健康数据失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/health-data")
    public Result<String> addHealthData(@RequestBody HealthDataReq req) {
        try {
            // 通过用户名查找用户
            Optional<User> userOptional = userService.findByUsername(req.getUsername());
            if (!userOptional.isPresent()) {
                return Result.error("未找到用户: " + req.getUsername());
            }
            
            User user = userOptional.get();
            
            HealthData healthData = new HealthData();
            healthData.setUserId(user.getId());
            healthData.setHeartRate(req.getHeartRate());
            healthData.setBloodSugar(req.getBloodSugar());
            healthData.setHealthStatus(req.getHealthStatus());
            
            healthDataService.saveHealthData(healthData);
            return Result.success("健康数据录入成功");
        } catch (Exception e) {
            return Result.error("健康数据录入失败: " + e.getMessage());
        }
    }
    
    @Data
    static class HealthDataReq {
        private String username;
        private Integer heartRate;
        private Double bloodSugar;
        private String healthStatus;
    }
}