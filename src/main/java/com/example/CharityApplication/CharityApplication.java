package com.example.CharityApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example")
@EnableJpaRepositories(basePackages = "com.example.repository")
@EntityScan(basePackages = "com.example.entity")
public class CharityApplication {
    public static void main(String[] args) {
    	System.setProperty("java.awt.headless", "false");
        SpringApplication.run(CharityApplication.class, args);
    }
}
