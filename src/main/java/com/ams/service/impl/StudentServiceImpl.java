package com.ams.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ams.model.StudentDetails;
import com.ams.repository.StudentRepository;
import com.ams.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<StudentDetails> getAllStudents() {
        return studentRepository.findAll(); // This works if StudentRepository extends JpaRepository<StudentDetails, Long>
    }

    @Override
    public void saveStudent(StudentDetails student) {
        studentRepository.save(student);
    }

    @Override
    public StudentDetails getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }
    
    @Override
    public StudentDetails getStudentByEmail(String email)
    {
    	return studentRepository.findByUserEmail(email);
    }
}