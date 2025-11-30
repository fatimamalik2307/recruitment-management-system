package com.recruitment.app.models;

import java.time.LocalDateTime;

public class AssessmentResult {
    private int id;
    private int shortlistId;
    private int recruiterId;
    private double technicalScore;
    private double hrScore;
    private String remarks;
    private LocalDateTime recordedAt;

    public AssessmentResult() {}

    public AssessmentResult(int shortlistId, int recruiterId, double technicalScore, double hrScore, String remarks) {
        this.shortlistId = shortlistId;
        this.recruiterId = recruiterId;
        this.technicalScore = technicalScore;
        this.hrScore = hrScore;
        this.remarks = remarks;
        this.recordedAt = LocalDateTime.now();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getShortlistId() { return shortlistId; }
    public void setShortlistId(int shortlistId) { this.shortlistId = shortlistId; }

    public int getRecruiterId() { return recruiterId; }
    public void setRecruiterId(int recruiterId) { this.recruiterId = recruiterId; }

    public double getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(double technicalScore) { this.technicalScore = technicalScore; }

    public double getHrScore() { return hrScore; }
    public void setHrScore(double hrScore) { this.hrScore = hrScore; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
