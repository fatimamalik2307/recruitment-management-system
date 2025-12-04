package com.recruitment.app.services;

import com.recruitment.app.dao.*;
import com.recruitment.app.models.Application;
import com.recruitment.app.models.FinalRankedCandidate;
import com.recruitment.app.models.JobPosting;

import java.util.*;

public class HMServiceImpl implements HMService {
    private final FinalRankedCandidateDAO candidateDAO;
    private final ApplicationDAO applicationDAO;
    private final JobDAO jobDAO;
    private final NotificationService notificationService; // NEW

    // Updated constructor (without breaking old code)
    public HMServiceImpl(FinalRankedCandidateDAO candidateDAO,
                         ApplicationDAO applicationDAO,
                         JobDAO jobDAO,
                         NotificationService notificationService) {
        this.candidateDAO = candidateDAO;
        this.applicationDAO = applicationDAO;
        this.jobDAO = jobDAO;
        this.notificationService = notificationService; // NEW
    }

    // Backward-compatible constructor (for existing code)
    public HMServiceImpl(FinalRankedCandidateDAO candidateDAO,
                         ApplicationDAO applicationDAO,
                         JobDAO jobDAO) {
        this(candidateDAO, applicationDAO, jobDAO, null);
    }

    // ----------------------------------------------------
    // NEW METHOD (UC-28): Notify all candidates for job
    // ----------------------------------------------------
    @Override
    public boolean notifyCandidatesForJob(int jobPostingId) {

        if (notificationService == null) {
            System.err.println("[HMService] NotificationService not injected!");
            return false;
        }

        try {
            List<FinalRankedCandidate> candidates =
                    candidateDAO.findByJobPostingIdAndStatus(jobPostingId, "HM_REVIEWED");

            if (candidates == null || candidates.isEmpty()) {
                return false;
            }

            for (FinalRankedCandidate c : candidates) {

                Application app = applicationDAO.findById(c.getApplicationId());
                if (app == null) continue;

                String subject = "Your Job Application Status";
                String msg;

                switch (app.getStatus()) {
                    case "SELECTED":
                        msg = "Congratulations! You have been SELECTED for: "
                                + jobDAO.getJobById(jobPostingId).getTitle();
                        break;

                    case "REJECTED":
                        msg = "We appreciate your interest. Unfortunately, you were NOT SELECTED for: "
                                + jobDAO.getJobById(jobPostingId).getTitle();
                        break;

                    default:
                        msg = "Your application status has been updated: " + app.getStatus();
                }

                notificationService.sendNotificationToApplicant(
                        app.getUserId(),
                        subject,
                        msg
                );
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean sendToHiringManager(int jobPostingId) {
        try {
            System.out.println("[DEBUG HMService] sendToHiringManager called for job: " + jobPostingId);

            // Get all candidates for this job
            List<FinalRankedCandidate> candidates = candidateDAO.getByJobId(jobPostingId);
            System.out.println("[DEBUG HMService] Found " + candidates.size() + " candidates for job: " + jobPostingId);

            if (candidates.isEmpty()) {
                System.out.println("[INFO HMService] No candidates to send for this job.");
                return false;
            }

            int updatedCount = 0;

            // Update each candidate individually
            for (FinalRankedCandidate candidate : candidates) {
                System.out.println("[DEBUG HMService] Updating candidate ID: " + candidate.getId() +
                        " from status: '" + candidate.getStatus() + "' to 'SENT_TO_HM'");

                // Make sure DAO method returns true if update succeeded
                boolean updated = candidateDAO.updateStatusAndNotes(candidate.getId(), "SENT_TO_HM", candidate.getHmNotes());
                if (updated) {
                    updatedCount++;
                } else {
                    System.out.println("[WARNING HMService] Failed to update candidate ID: " + candidate.getId());
                }
            }

            System.out.println("[DEBUG HMService] Successfully updated " + updatedCount + " out of " + candidates.size() +
                    " candidates to 'SENT_TO_HM'");

            // Return true if at least one candidate was updated
            return updatedCount > 0;

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
