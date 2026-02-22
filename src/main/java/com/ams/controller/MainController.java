package com.ams.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String showHomePage() {
        // This tells Spring to look for index.html in /templates/ folder
        return "index"; 
    }
    
    @GetMapping("/login") 
    public String showLoginPage() {
        return "login"; 
    }
    
}