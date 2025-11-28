package com.recruitment.app.dao;

import com.recruitment.app.models.User;

public interface UserDAO {
    boolean register(User user);
    User login(String username, String password);
    boolean updateProfile(User user);   // ADD THIS

}
