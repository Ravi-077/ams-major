package com.ams.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ams.service.UserService;

import org.springframework.ui.Model;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService; 

    
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; 
    }


    @PostMapping("/register")
    public String registerUser(@RequestParam String name, 
                               @RequestParam String email, 
                               @RequestParam String password, 
                               @RequestParam String role, 
                               Model model) {
        
        // Let the Service decide if the user can be saved
        boolean isSaved = userService.registerNewUser(name, email, password, role);

        if (isSaved) {
            // Instead of redirecting, stay here and pass a success attribute
            model.addAttribute("registrationSuccess", true);
            return "register"; 
        } else {
            model.addAttribute("error", "User already exists with this email!");
            return "register"; 
        }
    }
}