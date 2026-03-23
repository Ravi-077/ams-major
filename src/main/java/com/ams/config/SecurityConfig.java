package com.ams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
@Configuration 
@EnableWebSecurity
public class SecurityConfig {

	// 1. DEDICATED ADMIN FILTER CHAIN (Evaluator Requirement)
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**") // This chain ONLY handles /admin URLs
            .authorizeHttpRequests(auth -> auth
                .anyRequest().hasRole("ADMIN") // Strict Role Check
            )
            .formLogin(form -> form
                .loginPage("/admin/login") // The "Secret" Login Page
                .loginProcessingUrl("/admin/login/process")
                .usernameParameter("email")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout=true")
                .deleteCookies("JSESSIONID")
            );
        return http.build();
    }

    // 2. MAIN FILTER CHAIN (For Students and Teachers)
    @Bean
    @Order(2)
    public SecurityFilterChain mainFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()
                .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .requestMatchers("/admin/**").denyAll()// Added for consistency
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .successHandler(customSuccessHandler()) // Keeps your existing routing logic
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
	    
	    @Bean
	       public AuthenticationSuccessHandler customSuccessHandler() {
	           return (request, response, authentication) -> {
	               var authorities = authentication.getAuthorities();
	               String redirectUrl = "/login?error"; // Default fallback

	               for (var authority : authorities) {
	                   if (authority.getAuthority().equals("ROLE_ADMIN")) {
	                       redirectUrl = "/Admindesh";
	                       break;
	                   } else if (authority.getAuthority().equals("ROLE_STUDENT")) {
	                       redirectUrl = "/StudentDashboard";
	                       break;
	                   } else if (authority.getAuthority().equals("ROLE_TEACHER")) {
	                       redirectUrl = "/teacherdesh";
	                       break;
	                   }
	               }
	               response.sendRedirect(redirectUrl);
	           };
	       }

	    @Bean(name = "passwordEncoder")
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    } 
	    
}