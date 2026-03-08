package com.ams.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ams.model.TeacherDetails;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherDetails, Long> {
	
	@Query("SELECT t FROM TeacherDetails t JOIN FETCH t.user")
    List<TeacherDetails> findAllWithUser();
}