package com.silver.controller;

import com.silver.entity.User;
import com.silver.service.UserService;
import com.silver.utils.Result;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping
public class ApiController {

    @Autowired
    private UserService userService;

    // --- 1. 认证模块 ---
    @PostMapping("/auth/login")
    public Result<Map<String, Object>> login(@RequestBody LoginReq req) {
        // 从数据库中查找用户
        Optional<User> userOptional = userService.findByUsername(req.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 验证密码（在实际应用中应该使用加密密码进行比较）
            if (user.getPassword().equals(req.getPassword())) {
                Map<String, Object> data = new HashMap<>();
                
                // 根据用户ID返回不同的token
                String token = "mock-jwt-token-silver-guardian-" + user.getId();
                
                data.put("token", token);
                data.put("user", Map.of(
                    "userId", user.getId(), 
                    "name", user.getName(), 
                    "role", user.getRole() != null ? user.getRole().name() : "CHILD"
                ));
                return Result.success(data);
            }
        }
        return Result.error("用户名或密码错误");
    }

    // --- 2. 注册模块 ---
    @PostMapping("/auth/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterReq req) {
        try {
            // 检查用户名是否已存在
            if (userService.findByUsername(req.getUsername()).isPresent()) {
                return Result.error("用户名已存在");
            }
            
            // 检查手机号是否已存在
            if (userService.findByPhoneNumber(req.getPhoneNumber()).isPresent()) {
                return Result.error("手机号已注册");
            }
            
            // 创建新用户
            User user = new User();
            user.setUsername(req.getUsername());
            user.setPassword(req.getPassword());
            user.setName(req.getName());
            user.setPhoneNumber(req.getPhoneNumber());
            user.setRole(User.Role.valueOf(req.getRole()));
            user.setGender(User.Gender.valueOf(req.getGender()));
            
            // 保存用户
            User savedUser = userService.save(user);
            
            // 返回成功响应
            Map<String, Object> data = new HashMap<>();
            data.put("userId", savedUser.getId());
            data.put("username", savedUser.getUsername());
            data.put("name", savedUser.getName());
            
            return Result.success(data, "注册成功");
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    // --- 4. 一键挂号模块 ---
    @PostMapping("/appointments/recommendations")
    public Result<Map<String, Object>> recommend(@RequestBody Map<String, Object> req) {
        String symptom = (String) req.get("symptomText");
        List<Map<String, Object>> recs = new ArrayList<>();
        
        // 简单模拟推荐逻辑
        if (symptom != null && symptom.contains("胸")) {
             recs.add(Map.of(
                "hospitalId", "h_001", "hospitalName", "市第一人民医院",
                "departmentId", "d_cardio", "departmentName", "心血管内科",
                "priority", 1, "reason", "症状匹配心血管风险"
            ));
        } else {
             recs.add(Map.of(
                "hospitalId", "h_002", "hospitalName", "社区卫生中心",
                "departmentId", "d_gen", "departmentName", "全科门诊",
                "priority", 2, "reason", "建议全科初诊"
            ));
        }
        return Result.success(Map.of("recommendations", recs));
    }

    @PostMapping("/appointments/quick")
    public Result<Map<String, Object>> quickAppoint(@RequestBody Map<String, Object> req) {
        return Result.success(Map.of(
            "appointmentId", "appt_" + System.currentTimeMillis(),
            "redirectUrl", "https://www.example.com/payment-mock"
        ));
    }

    // --- 5. 用户资料模块 ---
    @PutMapping("/user/profile")
    public Result<Map<String, Object>> updateUserProfile(@RequestBody UserProfileReq req) {
        // 在实际应用中，这里应该更新数据库中的用户信息
        // 查找用户（这里简化为查找第一个用户）
        Optional<User> userOptional = userService.findById(1L);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(req.getName());
            
            if (req.getRole() != null) {
                try {
                    user.setRole(User.Role.valueOf(req.getRole()));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的角色值
                }
            }
            
            if (req.getGender() != null) {
                try {
                    user.setGender(User.Gender.valueOf(req.getGender()));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的性别值
                }
            }
            
            if (req.getBirthday() != null && !req.getBirthday().isEmpty()) {
                try {
                    user.setBirthday(LocalDate.parse(req.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                } catch (Exception e) {
                    // 忽略无效的日期格式
                }
            }
            
            user.setIdNumberLast4(req.getIdNumberLast4());
            
            userService.save(user);
            
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            return Result.success(data, "资料更新成功");
        } else {
            return Result.error("用户不存在");
        }
    }

    @Data
    static class LoginReq {
        private String username;
        private String password;
    }

    @Data
    static class RegisterReq {
        private String username;
        private String password;
        private String name;
        private String phoneNumber;
        private String role;
        private String gender;
    }

    @Data
    static class UserProfileReq {
        private String name;
        private String role;
        private String gender;
        private String birthday;
        private String idNumberLast4;
    }
}