package com.recruitment.app.models;

import java.time.LocalDateTime;

public class PersonSpecification {
    private int id;
    private int recruiterId;
    private String skills;
    private String experience;
    private String education;
    private String traits;
    private LocalDateTime createdAt;

    public PersonSpecification() {}

    public PersonSpecification(int recruiterId, String skills, String experience, String education, String traits) {
        this.recruiterId = recruiterId;
        this.skills = skills;
        this.experience = experience;
        this.education = education;
        this.traits = traits;
        this.createdAt = LocalDateTime.now();
    }

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRecruiterId() { return recruiterId; }
    public void setRecruiterId(int recruiterId) { this.recruiterId = recruiterId; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getTraits() { return traits; }
    public void setTraits(String traits) { this.traits = traits; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
