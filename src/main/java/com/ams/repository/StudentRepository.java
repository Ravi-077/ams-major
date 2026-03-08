package com.ams.repository;

import com.ams.model.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<StudentDetails, Long> 
{	    
	 
	// This looks into the 'user' relationship and finds the 'email'
	    StudentDetails findByUserEmail(String email);	

}