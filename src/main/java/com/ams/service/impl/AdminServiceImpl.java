package com.ams.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ams.model.Admin;
import com.ams.repository.AdminRepository;
import com.ams.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }
    
    @Override
    public void updateProfile(Admin admin) {
        adminRepository.save(admin);
    }
}