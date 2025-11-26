package com.silver.utils;

import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    
    /**
     * 解析JWT Token获取用户信息
     * 在实际应用中，这里应该使用真正的JWT解析库
     * @param token JWT Token
     * @return 用户信息
     */
    public static Map<String, Object> parseToken(String token) {
        // 在实际应用中，这里应该解析真实的JWT Token
        // 目前我们根据token内容简单判断用户
        Map<String, Object> userInfo = new HashMap<>();
        
        // 这是一个简化的实现，实际应用中应该使用JWT库解析token
        if ("mock-jwt-token-silver-guardian".equals(token)) {
            userInfo.put("userId", 1L);
            userInfo.put("username", "testuser");
            userInfo.put("name", "小周");
        } else if ("mock-jwt-token-silver-guardian-2".equals(token)) {
            userInfo.put("userId", 2L);
            userInfo.put("username", "zhouzhou");
            userInfo.put("name", "小周爸爸");
        } else if ("mock-jwt-token-silver-guardian-3".equals(token)) {
            userInfo.put("userId", 3L);
            userInfo.put("username", "mao");
            userInfo.put("name", "小周妈妈");
        } else if ("mock-jwt-token-silver-guardian-4".equals(token)) {
            userInfo.put("userId", 4L);
            userInfo.put("username", "zhouzhou2");
            userInfo.put("name", "小周孩子");
        } else if ("mock-jwt-token-silver-guardian-5".equals(token)) {
            userInfo.put("userId", 5L);
            userInfo.put("username", "zhouzhou3");
            userInfo.put("name", "路人甲");
        } else if ("mock-jwt-token-silver-guardian-6".equals(token)) {
            userInfo.put("userId", 6L);
            userInfo.put("username", "宇俊");
            userInfo.put("name", "宇俊");
        } else if (token != null && token.startsWith("mock-jwt-token-silver-guardian-")) {
            // 处理其他新注册用户
            try {
                String idStr = token.replace("mock-jwt-token-silver-guardian-", "");
                Long userId = Long.parseLong(idStr);
                userInfo.put("userId", userId);
                userInfo.put("username", "user" + userId);
                userInfo.put("name", "用户" + userId);
            } catch (NumberFormatException e) {
                // 默认用户
                userInfo.put("userId", 1L);
                userInfo.put("username", "testuser");
                userInfo.put("name", "小周");
            }
        } else {
            // 默认用户
            userInfo.put("userId", 1L);
            userInfo.put("username", "testuser");
            userInfo.put("name", "小周");
        }
        
        return userInfo;
    }
}