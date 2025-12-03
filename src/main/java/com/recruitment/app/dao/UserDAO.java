package com.recruitment.app.dao;

import com.recruitment.app.models.User;

public interface UserDAO {
    boolean register(User user);
    User login(String username, String password); // returns user object (password not exposed)
    boolean updateProfile(User user);
    boolean roleExistsForCompany(String role, int companyId);

    User findHiringManagerByRecruiterId(int recruiterId);

    // New method to fetch a user linked to a specific application
    User getByApplicationId(int applicationId);

    // Optional: fetch by userId directly
    User getById(int userId);
    boolean updatePassword(int id, String hashedPassword);

}
