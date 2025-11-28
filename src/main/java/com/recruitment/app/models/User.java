package com.recruitment.app.models;

public class User {

    private int id;
    private String fullName;
    private String email;
    private String username;
    private String password;
    private String contact;
    private String role;
    private String status;

    public User() {}

    public User(String fullName, String email, String username, String password, String contact) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.role = "applicant";
        this.status = "pending_verification";
    }

    // ------- GETTERS -------
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getContact() { return contact; }
    public String getRole() { return role; }
    public String getStatus() { return status; }

    // ------- SETTERS -------
    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setContact(String contact) { this.contact = contact; }
    public void setRole(String role) { this.role = role; }
    public void setStatus(String status) { this.status = status; }
}
