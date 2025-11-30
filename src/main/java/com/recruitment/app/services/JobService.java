package com.recruitment.app.services;

import com.recruitment.app.models.JobPosting;
import java.util.List;

public interface JobService {
    // Existing method
    boolean createJob(JobPosting job);

    // NEW METHODS FOR HM
   JobPosting getJobById(int jobId);
    List<JobPosting> getAllJobsForRecruiter(int recruiterId);
}