package com.ams.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ams.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
   
    Optional<Course> findByCourseCode(String courseCode);
    
    
}