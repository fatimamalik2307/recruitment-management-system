package com.recruitment.app.models;

import java.time.LocalDate;

/**
 * Represents a Job Posting in the system.
 * Follows SRP: only stores job attributes.
 */
public class JobPosting {
    private int id;
    private String title;
    private String department;
    private String description;
    private String requiredQualification;
    private String jobLocation;
    private LocalDate deadline;
    private String jobType; // Full-Time, Internship, etc.
    private String salaryRange;
    private String status; // Active / Closed
    private int createdBy; // Recruiter ID

    // Default constructor
    public JobPosting() {}

    // Full constructor
    public JobPosting(String title, String department, String description, String requiredQualification,
                      String jobLocation, LocalDate deadline, String jobType, String salaryRange, int createdBy) {
        this.title = title;
        this.department = department;
        this.description = description;
        this.requiredQualification = requiredQualification;
        this.jobLocation = jobLocation;
        this.deadline = deadline;
        this.jobType = jobType;
        this.salaryRange = salaryRange;
        this.status = "Active";
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredQualification() {
        return requiredQualification;
    }

    public void setRequiredQualification(String requiredQualification) {
        this.requiredQualification = requiredQualification;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}
