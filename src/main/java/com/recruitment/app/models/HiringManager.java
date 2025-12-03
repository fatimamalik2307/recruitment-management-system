package com.recruitment.app.models;

/**
 * Represents a Hiring Manager user.
 * Inherits all fields and behavior from User.
 */
public class HiringManager extends User {

    // Default constructor
    public HiringManager(String fullName, String email, String username, String password, String contact) {
        // Call the full constructor of User with role="Hiring Manager"
        super(fullName, email, username, password, contact, "Hiring Manager", null);
    }
}
