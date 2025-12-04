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
    private String required_qualification;
    private String job_location;
    private LocalDate deadline;
    private String job_type; // Full-Time, Internship, etc.
    private String salaryRange;
    private String status; // Active / Closed
    private int createdBy; // Recruiter ID
    private int recruiterId;
    private int hiringManagerId; // Add this

    // Getters and setters
    public int getHiringManagerId() { return hiringManagerId; }
    public void setHiringManagerId(int hiringManagerId) { this.hiringManagerId = hiringManagerId; }

    // Default constructor
    public JobPosting() {}

    // Full constructor
    public JobPosting(String title, String department, String description, String required_qualification,
                      String jobLocation, LocalDate deadline, String jobType, String salaryRange, int createdBy) {
        this.title = title;
        this.department = department;
        this.description = description;
        this.required_qualification = required_qualification;
        this.job_location = jobLocation;
        this.deadline = deadline;
        this.job_type = jobType;
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
        return required_qualification;
    }

    public void setRequiredQualification(String requiredQualification) {
        this.required_qualification = requiredQualification;
    }

    public String getJobLocation() {
        return job_location;
    }

    public void setJobLocation(String jobLocation) {
        this.job_location = jobLocation;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getJobType() {
        return job_type;
    }

    public void setJobType(String jobType) {
        this.job_type = jobType;
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

    public String getJobTitle() {
        return title;
    }

    public int getRecruiterId() {
        return recruiterId;
    }

    public void setJobTitle(String jobTitle) {
        this.title = jobTitle;
    }

    public void setRecruiterId(int recruiterId) {
        this.recruiterId = recruiterId;
    }

    @Override
    public String toString() {
        return title + " (" + department + ")";
    }

}
