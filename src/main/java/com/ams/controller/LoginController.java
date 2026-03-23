package com.ams.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.ams.service.UserService;

@Controller
public class LoginController {

	  private final UserService userService;

	    // This constructor tells Spring to plug in the Repository for you
       public LoginController (UserService userService) {
	        this.userService = userService;
      }
       
       @GetMapping("/login") 
       public String showLoginPage() {
           return "login"; 
       }
       
       @GetMapping("/teacherdesh") 
       public String showTeacherDashboard() {
           return "teacherdesh"; 
       }
       
       @GetMapping("/StudentDashboard")
       public String showStudentDashboard()
       {
    	   return "StudentDashboard";
       }
       
       @GetMapping("/admin/login")
       public String showAdminLoginPage() {
           return "admin/admin-login";
       }
	
}