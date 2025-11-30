package com.recruitment.app.models;

import java.time.LocalDateTime;

public class Shortlist {
    private int id;
    private int criteriaId;
    private int applicationId;
    private LocalDateTime shortlistedAt;

    public Shortlist() {}

    public Shortlist(int criteriaId, int applicationId) {
        this.criteriaId = criteriaId;
        this.applicationId = applicationId;
        this.shortlistedAt = LocalDateTime.now();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCriteriaId() { return criteriaId; }
    public void setCriteriaId(int criteriaId) { this.criteriaId = criteriaId; }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public LocalDateTime getShortlistedAt() { return shortlistedAt; }
    public void setShortlistedAt(LocalDateTime shortlistedAt) { this.shortlistedAt = shortlistedAt; }
}
