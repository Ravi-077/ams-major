package com.ams.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ams.model.StudentDetails;
import com.ams.model.TeacherDetails;
import com.ams.model.User;
import com.ams.repository.UserRepository;

@Service
public class UserService {

    // This is the connection to your MySQL Repository
    private final UserRepository userRepository;

   
    // This constructor tells Spring to plug in the Repository for you
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
 // return mail id from userRepo to AdminController

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // 1. Method for Student Registration
    public void registerUser(User user) {
        user.setRole("STUDENT");       
        user.setEnabled(false);        // Account is locked at first
        user.setStatus("PENDING");     
        userRepository.save(user);     // Hibernate does the INSERT query
    }

    // 2. Method for Admin Approval
    public void approveUser(Long userId) {
        // Find the user by their ID
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setEnabled(true);      // Account is now unlocked
        user.setStatus("APPROVED"); 
        userRepository.save(user);  // Hibernate does the UPDATE query
    }
    
    public String authenticateAndGetRole(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get(); 
            
            if (user.getPassword().equals(password)) {
                // STEP 1: Check the Blocker first
                if ("PENDING".equals(user.getStatus())) {
                    return "PENDING_APPROVAL";
                }
                // STEP 2: Only if NOT pending, return their actual role
                return user.getRole();
            }
        }
        return "INVALID";
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null); 
    }
    
    public boolean registerNewUser(String name, String email, String password, String role) {
        if (userRepository.existsByEmail(email)) {
            return false; // Email exits in db already
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus("PENDING"); // Admin will allow it

        userRepository.save(user); // Save to DB
        return true;
    }
    
 // Add this to your UserService class
    public List<User> findPendingByRole(String role) {
        return userRepository.findByRoleAndStatus(role, "PENDING");
    }
    
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
    
    
    
    @Transactional
    public void activateAndAssign(Long userId, String rollNo, String department, 
                                 String course, String year, String employeeId, 
                                 String designation, String qualification, String phone) {
        
    	User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setStatus("ACTIVE");

        if ("STUDENT".equals(user.getRole())) {
            StudentDetails student = new StudentDetails();
            student.setRollNumber(rollNo); // Captured
            student.setDepartment(department); // Was this missing?
            student.setCourse(course); // Was this missing?
            student.setYear(year); // Was this missing?
            student.setUser(user);
            user.setStudentDetails(student);
        } else if ("TEACHER".equals(user.getRole())) {
            TeacherDetails teacher = new TeacherDetails();
            teacher.setEmployeeId(employeeId); // Captured
            teacher.setDesignation(designation); // Was this missing?
            teacher.setDepartment(department); // Was this missing?
            teacher.setQualification(qualification); // Was this missing?
            teacher.setPhoneNumber(phone); // Was this missing?
            teacher.setUser(user);
            user.setTeacherDetails(teacher);
        }
        userRepository.save(user);
    }
}
