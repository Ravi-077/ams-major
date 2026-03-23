package com.ams.service;

import java.util.List;
import com.ams.model.TeacherDetails;

public interface TeacherService {
    List<TeacherDetails> getAllTeachers();
    TeacherDetails getById(long id);
    long getTotalTeacherCount();
    void saveTeacher(TeacherDetails teacher);
}