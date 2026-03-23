package com.ams.service;

import com.ams.model.StudentDetails; // Using your actual model name
import java.util.List;
import java.util.Optional;

public interface StudentService {
	
	StudentDetails getStudentByEmail(String email);
    List<StudentDetails> getAllStudents();
    void saveStudent(StudentDetails student);
    StudentDetails getStudentById(Long id);
    void deleteStudentById(Long id);
    long getTotalStudentCount();
}