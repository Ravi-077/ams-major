package com.ams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.ams") // Force scan everything in com.ams
@EnableJpaRepositories("com.ams.repository") // Force scan repositories
@ComponentScan(basePackages = {"com.ams"})
public class AcademicManagementSystemMajorApplication {
	
    public static void main(String[] args) 
    {
        SpringApplication.run(AcademicManagementSystemMajorApplication.class, args);
    }
}