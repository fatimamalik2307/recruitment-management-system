package com.recruitment.app.services;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.User;

import java.util.List;

public interface JobService {
    List<JobPosting>  getAllJobs();
    User getHiringManagerForRecruiter(int recruiterId);
    // Existing method
    boolean createJob(JobPosting job);

    // NEW METHODS FOR HM
   JobPosting getJobById(int jobId);
    List<JobPosting> getAllJobsForRecruiter(int recruiterId);
}