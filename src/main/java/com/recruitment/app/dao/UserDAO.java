package com.recruitment.app.dao;

import com.recruitment.app.models.User;

public interface UserDAO {
    boolean register(User user);
    User login(String username, String password); // returns user object (password not exposed)
    boolean updateProfile(User user);
    boolean roleExistsForCompany(String role, int companyId);
}
