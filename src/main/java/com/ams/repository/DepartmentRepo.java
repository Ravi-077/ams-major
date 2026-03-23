package com.ams.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ams.model.Department;

@Repository
public interface DepartmentRepo  extends JpaRepository<Department, Long> {
   
	Optional<Department> findByDeptCode(String deptCode);
	
}