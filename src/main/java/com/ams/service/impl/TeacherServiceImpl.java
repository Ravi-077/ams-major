package com.ams.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ams.model.TeacherDetails;
import com.ams.repository.TeacherRepository;
import com.ams.service.TeacherService;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public List<TeacherDetails> getAllTeachers() {
        return teacherRepository.findAllWithUser();
    }
    
    @Override
    public long getTotalTeacherCount() {
        return teacherRepository.count(); //"SELECT COUNT(*) FROM Teacher table"
    
        
    }
    
    @Override
    public TeacherDetails getById(long id)
    {
    	return teacherRepository.findById(id).orElse(null);
    }
    
    @Override
    public void saveTeacher(TeacherDetails teacher)
    {
    	 teacherRepository.save(teacher);
    }

}