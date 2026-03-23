package com.ams.service;


import java.util.Optional;

import com.ams.model.Department;

public interface DepartmentService 
{
	void saveDepartment(Department dept);
	Optional<Department> getDepartmentById(Long id);
}
