package com.silver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.silver.entity")
@EnableJpaRepositories(basePackages = "com.silver.repository")
public class SilverGuardianApplication {
    public static void main(String[] args) {
        SpringApplication.run(SilverGuardianApplication.class, args);
    }
}