package com.recruitment.app.models;

import java.time.LocalDateTime;

public class FinalRankedCandidate {
    private int id;
    private int applicationId;
    private int jobId;
    private Double compositeScore;
    private Integer rank;
    private String status; // Pending / Accepted / Rejected
    private String hmNotes; // Notes from Hiring Manager
    private LocalDateTime generatedAt;

    // NEW: store applicant name
    private String applicantName;

    public FinalRankedCandidate() {}

    public FinalRankedCandidate(int applicationId, int jobId, Double compositeScore, Integer rank, String applicantName) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.compositeScore = compositeScore;
        this.rank = rank;
        this.applicantName = applicantName;
        this.status = "Pending";
        this.generatedAt = LocalDateTime.now();
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public Double getCompositeScore() { return compositeScore; }
    public void setCompositeScore(Double compositeScore) { this.compositeScore = compositeScore; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHmNotes() { return hmNotes; }
    public void setHmNotes(String hmNotes) { this.hmNotes = hmNotes; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
}
