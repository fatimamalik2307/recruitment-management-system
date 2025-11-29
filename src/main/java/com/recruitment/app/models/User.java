package com.recruitment.app.models;

/**
 * User model - Single Responsibility: only holds user data.
 * Open for extension (constructors) but closed for modification.
 */
public class User {
    private int id;
    private String fullName;
    private String email;
    private String username;
    private String password;   // hashed password stored
    private String contact;
    private String role;       // "Applicant", "Recruiter", "Hiring Manager"
    private String status;     // "pending_verification", "active", etc.
    private Integer companyId; // nullable - only for recruiters/hiring managers

    public User() {}

    // Applicant convenience constructor
    public User(String fullName, String email, String username, String password, String contact) {
        this(fullName, email, username, password, contact, "Applicant", null);
    }

    // Full constructor - use for recruiters / hiring managers
    public User(String fullName, String email, String username, String password,
                String contact, String role, Integer companyId) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.role = role;
        this.companyId = companyId;
        this.status = "pending_verification";
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }
}
