package com.recruitment.app.models;

public class FinalRankingCriteria {
    private int id;
    private int jobId;
    private double technicalWeight;
    private double hrWeight;
    private double criteriaWeight;

    public FinalRankingCriteria() {}

    public FinalRankingCriteria(int jobId, double technicalWeight, double hrWeight, double criteriaWeight) {
        this.jobId = jobId;
        this.technicalWeight = technicalWeight;
        this.hrWeight = hrWeight;
        this.criteriaWeight = criteriaWeight;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public double getTechnicalWeight() { return technicalWeight; }
    public void setTechnicalWeight(double technicalWeight) { this.technicalWeight = technicalWeight; }
    public double getHrWeight() { return hrWeight; }
    public void setHrWeight(double hrWeight) { this.hrWeight = hrWeight; }
    public double getCriteriaWeight() { return criteriaWeight; }
    public void setCriteriaWeight(double criteriaWeight) { this.criteriaWeight = criteriaWeight; }
}
