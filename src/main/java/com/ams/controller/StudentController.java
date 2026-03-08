package com.ams.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ams.model.StudentDetails;
import com.ams.service.StudentService;

public class StudentController {
	
	@Autowired
	private StudentService studentService;
	
	@GetMapping("/StudentDashboard")
	public String showStudentDashboard(Principal principal, Model model) {
	    // Get the logged-in student's email
	    String email = principal.getName(); 
	    
	    System.out.println("DEBUG: Looking for student with email: " + email);

	    
	    StudentDetails student = studentService.getStudentByEmail(email); 
	    
	    System.out.println("DEBUG: Student Object found: " + student);
	    
	    // Pass the SINGLE student object to the page
	    model.addAttribute("student", student); 
	    
	    return "StudentDashboard"; 
	}

}
