//package com.ams.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration 
//public class SecurityConfig {
//
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//	    http
//	        .authorizeHttpRequests(auth -> auth
//	            .requestMatchers("/", "/login", "/css/**", "/images/**").permitAll() // Allow these!
//	            .anyRequest().authenticated()
//	        )
//	        .formLogin(form -> form
//	            .loginPage("/login") // This must match your @GetMapping("/login")
//	            .permitAll()
//	        );
//	    return http.build();
//	}
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        // Tells Spring to compare passwords exactly as they are in MySQL
//        return NoOpPasswordEncoder.getInstance(); 
//    }
//}