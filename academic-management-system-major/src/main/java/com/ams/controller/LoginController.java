package com.ams.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ams.model.User;
import com.ams.service.UserService;

@Controller
public class LoginController {

	  private final UserService userService;

	    // This constructor tells Spring to plug in the Repository for you
       public LoginController (UserService userService) {
	        this.userService = userService;
      }
	
       @PostMapping("/login")
       public String handleLogin(@RequestParam String email, @RequestParam String password, Model model) {
           // 1. Get the result from the Service
           String authResult = userService.authenticateAndGetRole(email, password);

           // 2. Handle Admin
           if (authResult.equals("ADMIN")) {
               User loggedInUser = userService.findByEmail(email);
               model.addAttribute("userName", loggedInUser.getName());
               return "Admindesh"; 
           } 
           // 3. Handle Active Students/Teachers
           else if (authResult.equals("STUDENT") || authResult.equals("TEACHER")) {
               return "redirect:/index"; 
           } 
           // 4. NEW: Handle Pending Users
           else if (authResult.equals("PENDING_APPROVAL")) {
               return "redirect:/login?pending=true"; 
           } 
           // 5. Handle wrong email/password
           else {
               return "redirect:/login?error=true"; 
           }
       }
}