package com.recruitment.app.models;

import java.time.LocalDateTime;

public class RecruitmentReport {

    private int id;
    private int jobId;
    private int recruiterId;
    private LocalDateTime generatedAt;
    private int totalApplications;
    private int totalShortlisted;
    private String jobTitle;
    private String department;
    private String postedOn;

    public RecruitmentReport() {}

    public RecruitmentReport(int jobId, int recruiterId, int totalApplications, int totalShortlisted) {
        this.jobId = jobId;
        this.recruiterId = recruiterId;
        this.totalApplications = totalApplications;
        this.totalShortlisted = totalShortlisted;
        this.generatedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(int recruiterId) {
        this.recruiterId = recruiterId;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }

    public int getTotalShortlisted() {
        return totalShortlisted;
    }

    public void setTotalShortlisted(int totalShortlisted) {
        this.totalShortlisted = totalShortlisted;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }
}
