package com.recruitment.app.models;

/**
 * Represents an Applicant user.
 * Inherits all fields and behavior from User.
 */
public class Applicant extends User {

    // Default constructor
    public Applicant() {
        super(); // call User default constructor
        super.setRole("Applicant");
    }

    // Convenience constructor
    public Applicant(String fullName, String email, String username, String password, String contact) {
        super(fullName, email, username, password, contact); // uses existing Applicant constructor in User
    }
}
