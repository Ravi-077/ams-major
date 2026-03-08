package com.ams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
@Configuration 
@EnableWebSecurity
public class SecurityConfig {

	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/","/login", "/register", "/css/**", "/js/**").permitAll() // Public
	                .requestMatchers("/admin/**").hasRole("ADMIN") 
	                .requestMatchers("/student/**").hasRole("STUDENT")
	                .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                .loginPage("/login") 
	                .usernameParameter("email")
	                .successHandler(customSuccessHandler())
	                .failureHandler((request, response, exception) -> {
	                    System.out.println("DEBUG: Login Failed. Reason: " + exception.getMessage());
	                    response.sendRedirect("/login?error=true");
	                })
	                .permitAll()
	                )
//	            .logout(logout -> logout.permitAll());
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