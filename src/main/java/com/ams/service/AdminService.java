package com.ams.service;

import com.ams.model.Admin;

public interface AdminService {
    Admin getAdminByEmail(String email);
    void updateProfile(Admin admin);
}