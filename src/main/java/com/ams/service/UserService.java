package com.ams.service;

import java.util.List;
import com.ams.model.User;

public interface UserService {
    User findById(Long id);
    User findByEmail(String email);
    void registerUser(User user);
    boolean registerNewUser(String name, String email, String password, String role);
    void approveUser(Long userId);
    void deleteUserById(Long id);
    List<User> findPendingByRole(String role);
    String authenticateAndGetRole(String email, String password);
    
    void activateAndAssign(Long userId, String rollNo, String department, 
                           String course, String year, String employeeId, 
                           String designation, String qualification, String phone);
}