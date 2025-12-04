package com.recruitment.app.services;

import com.recruitment.app.models.Application;
import com.recruitment.app.models.FinalRankedCandidate;
import com.recruitment.app.models.JobPosting;
import java.util.List;
import java.util.Map;

public interface HMService {
    // Candidate Management
    boolean sendToHiringManager(int jobPostingId);
    List<FinalRankedCandidate> getCandidatesSentToHM(int jobPostingId);


    List<JobPosting> getJobsForHM(int hmId);

    List<FinalRankedCandidate> getCandidatesByJobAndStatus(int jobPostingId, String status);

    // Decision Making
    boolean updateHiringDecision(int applicationId, String decision);
    boolean updateCandidateStatus(int candidateId, String status);
    Map<String, Integer> getDecisionStatistics(int jobPostingId);

    // Job Management

    JobPosting getJobPostingWithCandidateCount(int jobPostingId);

    // Application Details
    boolean finalizeHiringDecision(int applicationId, String decision, String notes);

    boolean hasRecruiterSubmittedFinalList(int jobId);


}