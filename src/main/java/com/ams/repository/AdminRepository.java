package com.ams.repository;

import com.ams.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    // This line tells Spring to generate the SQL: SELECT * FROM admin WHERE email = ?
    Optional<Admin> findByEmail(String email); 
}