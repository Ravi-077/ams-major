package com.ams.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ams.model.Course;
import com.ams.model.StudentDetails;
import com.ams.model.User;
import com.ams.repository.UserRepository;
import com.ams.service.AdminService;
import com.ams.service.CourseService;
import com.ams.service.EmailService;
import com.ams.service.StudentService;
import com.ams.service.TeacherService;
import com.ams.service.UserService;

@Controller
public class AdminController {

	@Autowired
	UserService  userService;
	
	@Autowired
    private EmailService emailService; // Add this line
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private TeacherService teacherService;

	@Autowired
	private CourseService courseService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired	
	private UserRepository userRepository;

	
	@GetMapping("/admin/teachers")
	public String showTeacherManagement(Model model) {
	    model.addAttribute("teachers", teacherService.getAllTeachers());
	    model.addAttribute("activePage", "teachers"); // This highlights 'Teacher Management' in the menu
		
	    return "teacher-management";
	}
	
	@GetMapping("/admin/students")
	public String listStudents(Model model) {
	    // 1. Fetch all students from MySQL via Service
	    List<StudentDetails> studentList = studentService.getAllStudents();
	    model.addAttribute("students", studentList);
	    model.addAttribute("activePage", "students");
	   
	    return "student-management";
	}
	
	@GetMapping("/admin/courses")
	public String listCourses(Model model) {
	    // Fetch courses from your service
	   List<Course> courseList = courseService.getAllCourses();
	   model.addAttribute("courses", courseList);
	   model.addAttribute("activePage", "courses"); // Keeps the sidebar link highlighted	
	   return "course-management";
	}
	
	@GetMapping("/admin/settings")
	public String showSettings(Model model, Principal principal) {
	    // Fetching the admin profile to populate th:value
	    User admin = userService.findByEmail("admin@ams.com"); 
	    
	    model.addAttribute("admin", admin); 
	    model.addAttribute("activePage", "settings");
	    return "admin-setting";
	}
	
	
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

        
        if (reviewId != null) {
            User selected = userService.findById(reviewId);
            model.addAttribute("selectedUser", selected);
        }

        return "Admindesh";
    }
	
	
    @PostMapping("/admin/finalize-approval")
    public String finalizeApproval(@RequestParam Long userId, 
                                   @RequestParam(required = false) String rollNumber,
                                   @RequestParam(required = false) String department, // This is the deptCode (e.g., "CS")
                                   @RequestParam(required = false) String course,     // This is the courseCode (e.g., "BTECH")
                                   @RequestParam(required = false) String year,
                                   @RequestParam(required = false) String employeeId,
                                   @RequestParam(required = false) String designation,
                                   @RequestParam(required = false) String qualification,
                                   @RequestParam(required = false) String phone) {
        
        // 1. Process activation and entity assignment
        userService.activateAndAssign(userId, rollNumber, department, course, year, 
                                      employeeId, designation, qualification, phone);
        
        // 2. Fetch user to get email for the notification
        User user = userService.findById(userId); 

        // 3. Send notification
        emailService.sendApprovalEmail(user.getEmail(), user.getName(), designation, employeeId);
        
        return "redirect:/Admindesh"; 
    }
    
    @ModelAttribute
    public void addCountsToAllAdminPages(Model model) {
        model.addAttribute("pendingTeacherCount", userRepository.countByRoleAndStatus("TEACHER", "PENDING"));
        model.addAttribute("pendingStudentCount", userRepository.countByRoleAndStatus("STUDENT", "PENDING"));
    }
}
