package com.ams.config;


import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.ams.model.User;
import com.ams.repository.UserRepository;

@Component
public class AdminInitializer {

    private final UserRepository userRepository;

    // Constructor Injection: Spring will plug in the Repository here
    public AdminInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        // 1. Check if admin exists
        if (!userRepository.existsByEmail("admin@ams.com")) {
            
            // 2. Create the Admin object
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@ams.com");
            admin.setPassword("admin123");
            admin.setRole("ADMIN");
            admin.setEnabled(true);

            // 3. Save to MySQL
            userRepository.save(admin);
            System.out.println(">> Success: Admin account created!");
        } else {
            System.out.println(">> Info: Admin already exists.");
        }
    }
}
