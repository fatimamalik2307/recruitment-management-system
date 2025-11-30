package com.recruitment.app.services;

import com.recruitment.app.dao.*;
import com.recruitment.app.models.FinalRankedCandidate;
import com.recruitment.app.models.JobPosting;

import java.util.*;

public class HMServiceImpl implements HMService {
    private final FinalRankedCandidateDAO candidateDAO;
    private final ApplicationDAO applicationDAO;
    private final JobDAO jobDAO;

    public HMServiceImpl(FinalRankedCandidateDAO candidateDAO,
                         ApplicationDAO applicationDAO,
                         JobDAO jobDAO) {
        this.candidateDAO = candidateDAO;
        this.applicationDAO = applicationDAO;
        this.jobDAO = jobDAO;
    }

    @Override
    public boolean sendToHiringManager(int jobPostingId) {
        try {
            System.out.println("[DEBUG HMService] sendToHiringManager called for job: " + jobPostingId);

            // Get all candidates for this job
            List<FinalRankedCandidate> candidates = candidateDAO.getByJobId(jobPostingId);
            System.out.println("[DEBUG HMService] Found " + candidates.size() + " candidates for job: " + jobPostingId);

            boolean allUpdated = true;
            int updatedCount = 0;

            // Update each candidate individually (same approach as flagListReady)
            for (FinalRankedCandidate candidate : candidates) {
                System.out.println("[DEBUG HMService] Updating candidate ID: " + candidate.getId() +
                        " from status: '" + candidate.getStatus() + "' to 'SENT_TO_HM'");

                candidateDAO.updateStatusAndNotes(candidate.getId(), "SENT_TO_HM", candidate.getHmNotes());

            }

            System.out.println("[DEBUG HMService] Successfully updated " + updatedCount + " out of " + candidates.size() + " candidates to 'SENT_TO_HM'");
            return allUpdated && updatedCount > 0;

        } catch (Exception e) {
            System.err.println("[ERROR HMService] Exception in sendToHiringManager: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<FinalRankedCandidate> getCandidatesSentToHM(int jobPostingId) {
        try {
            return candidateDAO.findByJobPostingIdAndStatus(jobPostingId, "SENT_TO_HM");
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch candidates sent to HM", e);
        }
    }

    @Override
    public List<JobPosting> getJobsForHM(int hmId) {
        return jobDAO.getJobsForHMByCompany(hmId);
    }


    @Override
    public List<FinalRankedCandidate> getCandidatesByJobAndStatus(int jobPostingId, String status) {
        try {
            return candidateDAO.findByJobPostingIdAndStatus(jobPostingId, status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch candidates by job and status", e);
        }
    }

    @Override
    public boolean updateHiringDecision(int applicationId, String decision) {
        if (!isValidDecision(decision)) {
            throw new IllegalArgumentException("Invalid decision. Must be SELECTED or REJECTED");
        }

        try {
            return applicationDAO.updateHMDecision(applicationId, decision);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update hiring decision", e);
        }
    }

    @Override
    public boolean updateCandidateStatus(int candidateId, String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status");
        }

        try {
            return candidateDAO.updateStatus(candidateId, status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update candidate status", e);
        }
    }

    @Override
    public Map<String, Integer> getDecisionStatistics(int jobPostingId) {
        try {
            List<FinalRankedCandidate> candidates = candidateDAO.getByJobId(jobPostingId);
            Map<String, Integer> stats = new HashMap<>();

            stats.put("TOTAL", candidates.size());
            stats.put("PENDING", (int) candidates.stream()
                    .filter(c -> "SENT_TO_HM".equals(c.getStatus()))
                    .count());
            stats.put("REVIEWED", (int) candidates.stream()
                    .filter(c -> "HM_REVIEWED".equals(c.getStatus()))
                    .count());

            return stats;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch decision statistics", e);
        }
    }



    @Override
    public JobPosting getJobPostingWithCandidateCount(int jobPostingId) {
        try {
            JobPosting job = jobDAO.getJobById(jobPostingId);
            if (job != null) {
                List<FinalRankedCandidate> candidates = getCandidatesSentToHM(jobPostingId);
                // You can set candidate count as needed
            }
            return job;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch job posting with candidate count", e);
        }
    }

    @Override
    public boolean finalizeHiringDecision(int applicationId, String decision, String notes) {
        try {
            // Update application decision
            boolean decisionUpdated = updateHiringDecision(applicationId, decision);

            // You might want to save notes here or update candidate status
            if (decisionUpdated) {
                // Find the candidate and update status to HM_REVIEWED
                // This would require additional DAO method to find candidate by applicationId
            }

            return decisionUpdated;
        } catch (Exception e) {
            throw new RuntimeException("Failed to finalize hiring decision", e);
        }
    }

    private boolean isValidDecision(String decision) {
        return decision != null && decision.matches("SELECTED|REJECTED|PENDING");
    }

    private boolean isValidStatus(String status) {
        return status != null && status.matches("SENT_TO_HM|HM_REVIEWED|ARCHIVED");
    }
    @Override
    public boolean hasRecruiterSubmittedFinalList(int jobId) {
        // Delegate to DAO
        return candidateDAO.existsByJobPostingId(jobId);
    }
}