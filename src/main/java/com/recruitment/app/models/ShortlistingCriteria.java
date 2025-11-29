package com.recruitment.app.models;

import java.time.LocalDateTime;

public class ShortlistingCriteria {
    private int id;
    private int jobId;
    private Integer minExperience;
    private String requiredQualification;
    private String requiredSkills;
    private String optionalLocation;
    private String optionalGrade;
    private LocalDateTime createdAt;

    public ShortlistingCriteria() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public Integer getMinExperience() { return minExperience; }
    public void setMinExperience(Integer minExperience) { this.minExperience = minExperience; }
    public String getRequiredQualification() { return requiredQualification; }
    public void setRequiredQualification(String requiredQualification) { this.requiredQualification = requiredQualification; }
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }
    public String getOptionalLocation() { return optionalLocation; }
    public void setOptionalLocation(String optionalLocation) { this.optionalLocation = optionalLocation; }
    public String getOptionalGrade() { return optionalGrade; }
    public void setOptionalGrade(String optionalGrade) { this.optionalGrade = optionalGrade; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
