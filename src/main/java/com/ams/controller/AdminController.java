package com.ams.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ams.model.Course;
import com.ams.model.Department;
import com.ams.model.StudentDetails;
import com.ams.model.TeacherDetails;
import com.ams.model.User;
import com.ams.repository.CourseRepository;
import com.ams.repository.DepartmentRepo;
import com.ams.repository.UserRepository;
import com.ams.service.AdminService;
import com.ams.service.CourseService;
import com.ams.service.DepartmentService;
import com.ams.service.EmailService;
import com.ams.service.StudentService;
import com.ams.service.TeacherService;
import com.ams.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

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
	
	@Autowired
	private DepartmentRepo deptRepo;
	
	@Autowired
	private DepartmentService deptService;
	
	@Autowired
	private CourseRepository courseRepo;

	
	
	@GetMapping("/admin/teachers")
	public String showTeacherManagement(Model model) {
	    model.addAttribute("teachers", teacherService.getAllTeachers());
	    model.addAttribute("activePage", "teachers"); // This highlights 'Teacher Management' in the menu
		
	    return "admin/teacher-management";
	}
	
	@GetMapping("/admin/students")
	public String listStudents(Model model , HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	    response.setHeader("Pragma", "no-cache");
	    response.setHeader("Expires", "0");
	    // 1. Fetch all students from MySQL via Service
	    List<StudentDetails> studentList = studentService.getAllStudents();
	    model.addAttribute("students", studentList);
	    model.addAttribute("activePage", "students");
	   
	    return "admin/student-management";
	}
	
	@GetMapping("/admin/courses")
	public String listCourses(Model model) {
	    // Fetch courses from your service
	   List<Course> courseList = courseService.getAllCourses();
	   model.addAttribute("courses", courseList);
	   model.addAttribute("activePage", "courses"); // Keeps the sidebar link highlighted	
	   return "admin/course-management";
	}
	
	@GetMapping("/admin/settings")
	public String showSettings(Model model, Principal principal) {
	    // Fetching the admin profile to populate th:value
	    User admin = userService.findByEmail("admin@ams.com"); 
	    
	    model.addAttribute("admin", admin); 
	    model.addAttribute("activePage", "settings");
	    return "admin/admin-setting";
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
        return "redirect:/admin/Admindesh?view=" + (view != null ? view : "teachers");
    }
	
    
    
    @GetMapping("/admin/dashboard")
    public String showDashboard(@RequestParam(defaultValue = "teachers") String view, 
                               @RequestParam(required = false) Long reviewId, 
                               Model model) {
    	
    	long studentCount = studentService.getTotalStudentCount();
        long teacherCount = teacherService.getTotalTeacherCount();
        long courseCount = courseService.getTotalCourseCount();

        // Pass them to the HTML
        model.addAttribute("totalStudents", studentCount);
        model.addAttribute("totalTeachers", teacherCount);
        model.addAttribute("totalCourses", courseCount);
        
        // 1. Always send both lists so the counts and tables are ready
        model.addAttribute("pendingStudents", userService.findPendingByRole("STUDENT"));
        model.addAttribute("pendingTeachers", userService.findPendingByRole("TEACHER"));
        
        // 2. Tell the HTML which tab the Admin is looking at
        model.addAttribute("currentView", view);

        
        if (reviewId != null) {
            User selected = userService.findById(reviewId);
            model.addAttribute("selectedUser", selected);
        }

        return "admin/Admindesh";
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
                                   @RequestParam(required = false) String phone,
                                   @RequestParam(required = false) String assignedSubjects,
                                   @RequestParam(required = false) String assignedSection){
        
    	System.out.println(assignedSubjects);
        // 1. Process activation and entity assignment
        userService.activateAndAssign(userId, rollNumber, department, course, year, 
                                      employeeId, designation, qualification, phone,assignedSubjects,assignedSection);
        
        // 2. Fetch user to get email for the notification
        User user = userService.findById(userId); 

        // 3. Send notification
        emailService.sendApprovalEmail(user.getEmail(), user.getName(), designation, employeeId);
        
        return "redirect:/admin/dashboard"; 
    }
    
    @ModelAttribute
    public void addCountsToAllAdminPages(Model model) {
        model.addAttribute("pendingTeacherCount", userRepository.countByRoleAndStatus("TEACHER", "PENDING"));
        model.addAttribute("pendingStudentCount", userRepository.countByRoleAndStatus("STUDENT", "PENDING"));
    }
    
    @PostMapping("/admin/students/toggle-status/{id}")
    public String deactivateStudent(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        // Redirect back to the management page to see the updated status
        return "redirect:/admin/students";
    }
    
    @GetMapping("/admin/students/view/{id}")
    public String viewStudentProfile(@PathVariable Long id, Model model) {
        // We fetch the StudentDetails by the User ID
        StudentDetails student = studentService.getStudentById(id); 
        model.addAttribute("student", student);
        return "admin/student-profile"; 
    }
    
    @GetMapping("/admin/teachers/view/{id}")
    public String viewTeacherProfile(@PathVariable Long id, Model model) {
        // here We are fetching the TeacherDetails by the User ID
        TeacherDetails teacher = teacherService.getById(id); 
        model.addAttribute("teacher", teacher);
        return "admin/teacher-profile"; 
    }
    
    @GetMapping("/admin/students/edit/{id}")
    public String showEditFormStd(@PathVariable Long id, Model model) {
        StudentDetails student = studentService.getStudentById(id); 
        model.addAttribute("student", student);
        
        // We also need to send the list of Departments/Courses so the Admin can change them
        model.addAttribute("departments", deptRepo.findAll());
        model.addAttribute("courses", courseRepo.findAll());
        
        return "admin/edit-student"; 
    }
    
    @GetMapping("/admin/teachers/edit/{id}")
    public String showEditFormTeacher(@PathVariable Long id, Model model) {
        TeacherDetails teacher = teacherService.getById(id); 
        model.addAttribute("teacher", teacher);
        
        // We also need to send the list of Departments/Courses so the Admin can change them
        model.addAttribute("departments", deptRepo.findAll());
        model.addAttribute("courses", courseRepo.findAll());
        
        return "admin/edit-teacher"; 
    }
    
    @PostMapping("/teachers/update")
    public String updateStudent(@ModelAttribute("student") TeacherDetails updatedDetails, 
                                RedirectAttributes redirectAttributes) {
        try {
            // Fetche existing student to keep the User relationship & Status intact
        	TeacherDetails existingTeacher = teacherService.getById(updatedDetails.getId());
            
            
            if (existingTeacher != null) {
                
            	String name = updatedDetails.getUser().getName();
            	existingTeacher.getUser().setName(name); 
                   
                    existingTeacher.setAssignedSubjects(updatedDetails.getAssignedSubjects());
                    existingTeacher.setDesignation(updatedDetails.getDesignation());
                    existingTeacher.setPhoneNumber(updatedDetails.getPhoneNumber());
                   
             
                if (updatedDetails.getDepartment() != null && updatedDetails.getDepartment().getId() != null) {
                    Optional<Department> dept = deptService.getDepartmentById(updatedDetails.getDepartment().getId());
                    if (dept.isPresent()) {
                    	existingTeacher.setDepartment(dept.get());
                    }
                }          
                teacherService.saveTeacher(existingTeacher);
                
              
                
                redirectAttributes.addFlashAttribute("success", "Student record updated successfully! ✅");
            } else {
                redirectAttributes.addFlashAttribute("error", "Student not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Update failed: " + e.getMessage());
        }

        return "redirect:/admin/teachers";
    }
    
    @PostMapping("/students/update")
    public String updateStudent(@ModelAttribute("student") StudentDetails updatedDetails, 
                                RedirectAttributes redirectAttributes) {
        try {
            // Fetche existing student to keep the User relationship & Status intact
            StudentDetails existingStudent = studentService.getStudentById(updatedDetails.getId());
            
            
            if (existingStudent != null) {
                
                    // 1. Update the name in the StudentDetails table
                    existingStudent.setName(updatedDetails.getName());
                    
                    // 2. Update the name in the linked User table
                    if (existingStudent.getUser() != null) {
                        existingStudent.getUser().setName(updatedDetails.getName());
                    }
                existingStudent.setFatherName(updatedDetails.getFatherName());
                existingStudent.setMotherName(updatedDetails.getMotherName());
                existingStudent.setMobileNumber(updatedDetails.getMobileNumber());
                existingStudent.setGender(updatedDetails.getGender());
                existingStudent.setYear(updatedDetails.getYear());
                existingStudent.setAddress(updatedDetails.getAddress());
                existingStudent.setFeeStatus(updatedDetails.getFeeStatus());
                existingStudent.setDepartment(updatedDetails.getDepartment());
             
                if (updatedDetails.getDepartment() != null && updatedDetails.getDepartment().getId() != null) {
                    Optional<Department> dept = deptService.getDepartmentById(updatedDetails.getDepartment().getId());
                    if (dept.isPresent()) {
                        existingStudent.setDepartment(dept.get());
                    }
                }          
                studentService.saveStudent(existingStudent);
                
              
                
                redirectAttributes.addFlashAttribute("success", "Student record updated successfully! ✅");
            } else {
                redirectAttributes.addFlashAttribute("error", "Student not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Update failed: " + e.getMessage());
        }

        return "redirect:/admin/students";
    }
    
    @PostMapping("/admin/teacher/assign")
    public String assignSubject(@RequestParam Long teacherId, @RequestParam String subject) {
        TeacherDetails teacher = teacherService.getById(teacherId);
        teacher.setAssignedSubjects(subject);
        teacherService.saveTeacher(teacher);
        return "redirect:/admin/teacher-management";
    }
}
