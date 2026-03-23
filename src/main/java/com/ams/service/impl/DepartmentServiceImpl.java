package com.ams.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ams.model.Department;
import com.ams.repository.DepartmentRepo;
import com.ams.service.DepartmentService;

@Service
public class DepartmentServiceImpl implements DepartmentService {

	@Autowired
	private DepartmentRepo deptRepo;
	
	@Override
	public Optional<Department> getDepartmentById(Long id)
	{
		return deptRepo.findById(id);
	}
	
	@Override
	public void saveDepartment(Department dept)
	{
		deptRepo.save(dept);
	}
}
