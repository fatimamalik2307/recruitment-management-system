package com.recruitment.app.dao;

import com.recruitment.app.models.JobPosting;

import java.time.LocalDate;
import java.util.List;

public interface JobDAO {
    // Existing methods
    void addJob(JobPosting job);
    void updateDeadline(int jobId, LocalDate newDeadline);
    void updateJob(JobPosting job);

    void deleteJob(int jobId);

    JobPosting getJobById(int jobId);

    List<JobPosting> getAllJobs();

    List<JobPosting> getJobsByRecruiterId(int recruiterId);

    void closeJob(int jobId);

    List<JobPosting> getJobsByRecruiterAndStatus(int recruiterId, String status);
    List<JobPosting> getJobsForHMByCompany(int hmId);

}