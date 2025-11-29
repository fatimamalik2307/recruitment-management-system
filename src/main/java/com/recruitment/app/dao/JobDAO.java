package com.recruitment.app.dao;

import com.recruitment.app.models.JobPosting;
import java.util.List;

/**
 * Interface for Job Data Access
 * Follows DIP: Controller depends on this abstraction, not implementation.
 */
public interface JobDAO {
    void addJob(JobPosting job);

    List<JobPosting> getAllJobs();

    List<JobPosting> getJobsByRecruiter(int recruiterId);
    JobPosting getJobById(int jobId);
    void updateJob(JobPosting job);
    void deleteJob(int jobId);
}
