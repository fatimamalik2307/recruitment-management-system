package com.recruitment.app.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class JobDescription {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty recruiterId = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty duties = new SimpleStringProperty();
    private final StringProperty responsibilities = new SimpleStringProperty();
    private final StringProperty jobPurpose = new SimpleStringProperty();
    private final StringProperty reportingStructure = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    private String department;
    private String jobType;
    private String requiredQualification;

    public String getRequiredQualification() { return requiredQualification; }
    public void setRequiredQualification(String q) { this.requiredQualification = q; }

    // ---------------- NO-ARG ----------------
    public JobDescription() {}

    // Constructor used when saving new record
    public JobDescription(int recruiterId,
                          String title,
                          String duties,
                          String responsibilities,
                          String jobPurpose,
                          String reportingStructure,
                          String jobType,
                          String department) {

        this.recruiterId.set(recruiterId);
        this.title.set(title);
        this.duties.set(duties);
        this.responsibilities.set(responsibilities);
        this.jobPurpose.set(jobPurpose);
        this.reportingStructure.set(reportingStructure);
        this.jobType = jobType;
        this.department = department;
    }

    // Full constructor for DAO mapping
    public JobDescription(int id,
                          int recruiterId,
                          String title,
                          String duties,
                          String responsibilities,
                          String jobPurpose,
                          String reportingStructure,
                          LocalDateTime createdAt,
                          String jobType,
                          String department) {

        this.id.set(id);
        this.recruiterId.set(recruiterId);
        this.title.set(title);
        this.duties.set(duties);
        this.responsibilities.set(responsibilities);
        this.jobPurpose.set(jobPurpose);
        this.reportingStructure.set(reportingStructure);
        this.createdAt.set(createdAt);
        this.jobType = jobType;
        this.department = department;
    }

    // ------------------- GETTERS & SETTERS ---------------------

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public int getRecruiterId() { return recruiterId.get(); }
    public void setRecruiterId(int recruiterId) { this.recruiterId.set(recruiterId); }
    public IntegerProperty recruiterIdProperty() { return recruiterId; }

    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    public String getDuties() { return duties.get(); }
    public void setDuties(String duties) { this.duties.set(duties); }
    public StringProperty dutiesProperty() { return duties; }

    public String getResponsibilities() { return responsibilities.get(); }
    public void setResponsibilities(String responsibilities) { this.responsibilities.set(responsibilities); }
    public StringProperty responsibilitiesProperty() { return responsibilities; }

    public String getJobPurpose() { return jobPurpose.get(); }
    public void setJobPurpose(String jobPurpose) { this.jobPurpose.set(jobPurpose); }
    public StringProperty jobPurposeProperty() { return jobPurpose; }

    public String getReportingStructure() { return reportingStructure.get(); }
    public void setReportingStructure(String reportingStructure) { this.reportingStructure.set(reportingStructure); }
    public StringProperty reportingStructureProperty() { return reportingStructure; }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }


}
