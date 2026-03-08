package com.ams.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder; // Add this import
import org.springframework.stereotype.Component;

import com.ams.model.User;
import com.ams.repository.UserRepository;

@Component
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 1. Add the encoder variable

    // 2. Update Constructor to include PasswordEncoder
    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        if (!userRepository.existsByEmail("admin@ams.com")) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@ams.com");
            
            // 3. ENCODE THE PASSWORD HERE
            admin.setPassword(passwordEncoder.encode("admin123")); 
            
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            admin.setStatus("ACTIVE");

            userRepository.save(admin);
            System.out.println(">> Success: Admin account created with BCrypt hash!");
        } else {
            System.out.println(">> Info: Admin already exists.");
        }
    }
}