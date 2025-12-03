package com.recruitment.app.models;

/**
 * Represents a Recruiter user.
 * Inherits all fields and behavior from User.
 */
public class Recruiter extends User {

    // Default constructor
    public Recruiter(String fullName, String email, String username, String password, String contact) {
        // Call the full constructor of User with role="Recruiter"
        super(fullName, email, username, password, contact, "Recruiter", null);
    }

}
