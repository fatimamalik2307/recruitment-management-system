package com.recruitment.app.models;

public class JobPosting {

    private int id;
    private String jobTitle;
    private String department;
    private String description;
    private String requiredQualification;
    private String deadline;

    public JobPosting(int id, String jobTitle, String department, String description,
                      String requiredQualification, String deadline) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.department = department;
        this.description = description;
        this.requiredQualification = requiredQualification;
        this.deadline = deadline;
    }

    public int getId() { return id; }
    public String getJobTitle() { return jobTitle; }
    public String getDepartment() { return department; }
    public String getDescription() { return description; }
    public String getRequiredQualification() { return requiredQualification; }
    public String getDeadline() { return deadline; }
}
