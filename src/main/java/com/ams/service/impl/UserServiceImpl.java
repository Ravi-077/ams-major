package com.ams.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ams.model.Course;
import com.ams.model.Department;
import com.ams.model.StudentDetails;
import com.ams.model.TeacherDetails;
import com.ams.model.User;
import com.ams.repository.CourseRepository;
import com.ams.repository.DepartmentRepo;
import com.ams.repository.UserRepository;
import com.ams.service.UserService;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // For secure BCrypt hashing
    private final DepartmentRepo deptRepo;
    private final CourseRepository courseRepo;
   
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, DepartmentRepo deptRepo, CourseRepository courseRepo) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.deptRepo= deptRepo;
        this.courseRepo=courseRepo;
    }

    // --- SPRING SECURITY LOGIC ---
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailClean(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Forcefully extract the strings to ensure we aren't passing a proxy
        String dbEmail = user.getEmail();
        String dbPassword = user.getPassword();
        String dbRole = user.getRole();

        System.out.println("DEBUG: Final check for " + dbEmail + " | Hash: " + dbPassword);
       
        return org.springframework.security.core.userdetails.User.builder()
                .username(dbEmail)
                .password(dbPassword)
                .roles(dbRole)
                .disabled(false) // Explicitly tell Spring the account is NOT disabled
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }

    // --- BUSINESS LOGIC METHODS ---
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailClean(email).orElse(null); 
    }

    @Override
    public boolean registerNewUser(String name, String email, String password, String role) {
        if (userRepository.existsByEmail(email)) return false;

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        // ENCODE PASSWORD BEFORE SAVING
        user.setPassword(passwordEncoder.encode(password)); 
        user.setRole(role);
        user.setStatus("PENDING");
        userRepository.save(user);
        return true;
    }

    @Override
    public void registerUser(User user) {
        user.setRole("STUDENT");
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt
        user.setStatus("PENDING");
        userRepository.save(user);
    }

    @Override
    public void approveUser(Long userId) {
        User user = findById(userId);
        user.setEnabled(true);
        user.setStatus("APPROVED");
        userRepository.save(user);
    }

    @Override
    public List<User> findPendingByRole(String role) {
        return userRepository.findByRoleAndStatus(role, "PENDING");
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public String authenticateAndGetRole(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmailClean(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Match encoded password
            if (passwordEncoder.matches(password, user.getPassword())) { 
                return "PENDING".equals(user.getStatus()) ? "PENDING_APPROVAL" : user.getRole();
            }
        }
        return "INVALID";
    }
    
    
    
    @Transactional
    public void activateAndAssign(Long userId, String rollNumber, String deptCode, String courseCode, 
                                 String year, String empId, String desig, String qual, String phone) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus("ACTIVE");
        user.setEnabled(true); // Ensure they can log in now

        if ("STUDENT".equals(user.getRole())) {
            StudentDetails s = new StudentDetails();
            s.setRollNumber(rollNumber);
            s.setYear(year);
            s.setUser(user);

            // Fetch Department Entity by Code
            Department dept = deptRepo.findByDeptCode(deptCode)
                .orElseThrow(() -> new RuntimeException("Dept not found: " + deptCode));
            s.setDepartment(dept);

            // Fetch Course Entity by Code
            Course crs = courseRepo.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseCode));
            s.setCourse(crs);

            user.setStudentDetails(s);

        } else if ("TEACHER".equals(user.getRole())) {
            TeacherDetails t = new TeacherDetails();
            t.setEmployeeId(empId);
            t.setDesignation(desig);
            t.setQualification(qual);
            t.setPhoneNumber(phone);
            
            // If you updated TeacherDetails to use the Department Entity:
            Department dept = deptRepo.findByDeptCode(deptCode)
                .orElseThrow(() -> new RuntimeException("Dept not found"));
            t.setDepartment(dept); 

            t.setUser(user);
            user.setTeacherDetails(t);
        }
        
        userRepository.save(user); // Saves everything due to CascadeType.ALL
    }

//    @Override
//    @Transactional
//    public void activateAndAssign(Long userId, String rollNumber, String deptName, String courseName, 
//                                 String year, String empId, String desig, String qual, String phone) {
//        User user = findById(userId);
//        user.setStatus("ACTIVE");
//        user.setEnabled(true); // Ensure they can actually log in!
//
//        if ("STUDENT".equals(user.getRole())) {
//            StudentDetails s = new StudentDetails();
//            s.setRollNumber(rollNumber);
//            s.setYear(year);
//            
//            // Fetch the REAL objects from DB
//            Department d = deptRepo.findByName(deptName)
//                .orElseThrow(() -> new RuntimeException("Dept not found"));
//            Course c = courseRepo.findByCourseCode(courseName)
//                .orElseThrow(() -> new RuntimeException("Course not found"));
//                
//            s.setDepartment(d); // Assigning the reference
//            s.setCourse(c);     // Assigning the reference
//            
//            s.setUser(user); 
//            user.setStudentDetails(s);
//        } 
//        // ... same logic for Teacher department ...
//        userRepository.save(user);
//    }
}