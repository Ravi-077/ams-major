package com.ams.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ams.model.User;
import com.ams.service.EmailService;
import com.ams.service.UserService;

@Controller
public class AdminController {

	@Autowired
	UserService  userService;
	
	@Autowired
    private EmailService emailService; // Add this line

    @GetMapping("/admin/reject/{id}")
    public String rejectUser(@PathVariable Long id, @RequestParam(required = false) String view) {
        // 1. Get user details before they are gone from the DB
        User user = userService.findById(id); 
        
        // 2. Send the "Sorry" email
        emailService.sendRejectionEmail(user.getEmail(), user.getName()); 
        
        // 3. Perform the deletion
        userService.deleteUserById(id); 
        
        // 4. Stay on the current tab (Students or Teachers)
        return "redirect:/Admindesh?view=" + (view != null ? view : "teachers");
    }
	
    @GetMapping("/Admindesh")
    public String showDashboard(@RequestParam(defaultValue = "teachers") String view, 
                               @RequestParam(required = false) Long reviewId, 
                               Model model) {
        
        // 1. Always send both lists so the counts and tables are ready
        model.addAttribute("pendingStudents", userService.findPendingByRole("STUDENT"));
        model.addAttribute("pendingTeachers", userService.findPendingByRole("TEACHER"));
        
        // 2. Tell the HTML which tab the Admin is looking at
        model.addAttribute("currentView", view);

        // 3. If the Admin clicked 'Approve' (✅), find that specific user for the form
        if (reviewId != null) {
            User selected = userService.findById(reviewId);
            model.addAttribute("selectedUser", selected);
        }

        return "Admindesh";
    }
	
	
	@PostMapping("/admin/finalize-approval")
	public String finalizeApproval(@RequestParam Long userId, 
	                               @RequestParam(required = false) String rollNo,
	                               @RequestParam(required = false) String department,
	                               @RequestParam(required = false) String course,
	                               @RequestParam(required = false) String year,
	                               @RequestParam(required = false) String employeeId,
	                               @RequestParam(required = false) String designation,
	                               @RequestParam(required = false) String qualification,
	                               @RequestParam(required = false) String phone) {
	    
	    // Pass ALL 9 arguments here to match the UserService signature
	    userService.activateAndAssign(userId, rollNo, department, course, year, 
	                                  employeeId, designation, qualification, phone);
	    
	    User user = userService.findById(userId); 

	    emailService.sendApprovalEmail(user.getEmail(),user.getName(),designation,employeeId);
	    return "redirect:/Admindesh"; 
	}
}
