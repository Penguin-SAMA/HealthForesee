package com.silver.config;

import com.silver.entity.User;
import com.silver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有用户数据，如果没有则初始化
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("testuser");
            user.setPassword("123456"); // 在实际应用中应该加密
            user.setName("张三");
            user.setRole(User.Role.CHILD);
            user.setGender(User.Gender.MALE);
            
            userRepository.save(user);
            System.out.println("初始化用户数据完成");
        }
    }
}