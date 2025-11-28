package com.recruitment.app.services;

import com.recruitment.app.dao.UserDAO;
import com.recruitment.app.dao.UserDAOImpl;
import com.recruitment.app.models.User;

public class UserService {

    private final UserDAO userDAO = new UserDAOImpl();

    public boolean register(User user) {
        return userDAO.register(user);
    }

    public User login(String username, String password) {
        return userDAO.login(username, password);
    }

    public boolean updateProfile(User user) {
        return userDAO.updateProfile(user);
    }
}
