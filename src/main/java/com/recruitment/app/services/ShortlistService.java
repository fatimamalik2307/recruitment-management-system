package com.recruitment.app.services;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.Shortlist;

import java.time.LocalDate;
import java.util.List;

public interface ShortlistService {
    List<JobPosting> getJobsEligibleForShortlisting();
    void updateDeadline(int jobId, LocalDate newDeadline);
    List<Shortlist> generateShortlist(int jobId);   // Generate shortlist based on criteria
    List<Shortlist> getShortlistByJob(int jobId);   // Fetch already shortlisted candidates
    Shortlist addToShortlist(int criteriaId, int applicationId);  // Add single application
}
