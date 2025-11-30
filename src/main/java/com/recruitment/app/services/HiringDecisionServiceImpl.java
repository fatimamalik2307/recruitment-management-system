package com.recruitment.app.services;

import com.recruitment.app.dao.*;
import com.recruitment.app.models.ApplicantNote;
import com.recruitment.app.models.Application;
import java.util.*;
import java.util.stream.Collectors;

public class HiringDecisionServiceImpl implements HiringDecisionService {
    private final ApplicationDAO applicationDAO;
    private final FinalRankedCandidateDAO candidateDAO;
    private final ApplicantNoteDAO noteDAO;

    public HiringDecisionServiceImpl(ApplicationDAO applicationDAO,
                                     FinalRankedCandidateDAO candidateDAO,
                                     ApplicantNoteDAO noteDAO) {
        this.applicationDAO = applicationDAO;
        this.candidateDAO = candidateDAO;
        this.noteDAO = noteDAO;
    }

    @Override
    public boolean makeHiringDecision(Long applicationId, String decision, Long hiringManagerId, String notes) {
        try {
            if (!isValidDecision(decision)) {
                throw new IllegalArgumentException("Invalid decision: " + decision);
            }

            // Update application decision
            boolean decisionUpdated = applicationDAO.updateHMDecision(Math.toIntExact(applicationId), decision);

            // Add note if provided
            if (decisionUpdated && notes != null && !notes.trim().isEmpty()) {
                ApplicantNote note = new ApplicantNote(
                        applicationId,
                        hiringManagerId,
                        "Hiring Decision: " + decision + ". Notes: " + notes,
                        ApplicantNote.NoteType.INTERNAL
                );
                noteDAO.save(note);
            }

            // Update candidate status if it's a final decision
            if (decisionUpdated && !"PENDING".equals(decision)) {
                updateCandidateStatusByApplication(applicationId, "HM_REVIEWED");
            }

            return decisionUpdated;
        } catch (Exception e) {
            throw new RuntimeException("Failed to make hiring decision", e);
        }
    }

    @Override
    public boolean bulkUpdateHiringDecisions(Map<Long, String> decisions, Long hiringManagerId) {
        try {
            boolean allSuccess = true;

            for (Map.Entry<Long, String> entry : decisions.entrySet()) {
                boolean success = makeHiringDecision(entry.getKey(), entry.getValue(), hiringManagerId, "Bulk update");
                if (!success) {
                    allSuccess = false;
                }
            }

            return allSuccess;
        } catch (Exception e) {
            throw new RuntimeException("Failed to bulk update hiring decisions", e);
        }
    }

    @Override
    public List<Application> getApplicationsByDecision(Long hiringManagerId, String decision) {
        try {
            if (!isValidDecision(decision)) {
                throw new IllegalArgumentException("Invalid decision: " + decision);
            }

            // This would require a new method in ApplicationDAO to filter by HM decision
            // For now, return empty list - implement based on your database structure
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch applications by decision", e);
        }
    }

    @Override
    public Map<String, Integer> getHiringStatistics(Long hiringManagerId) {
        try {
            Map<String, Integer> stats = new HashMap<>();

            // These would require new DAO methods to get statistics by hiring manager
            // For now, return placeholder values
            stats.put("TOTAL_REVIEWED", 0);
            stats.put("SELECTED", 0);
            stats.put("REJECTED", 0);
            stats.put("PENDING", 0);
            stats.put("OFFER_EXTENDED", 0);

            return stats;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch hiring statistics", e);
        }
    }

    @Override
    public boolean revertHiringDecision(Long applicationId, Long hiringManagerId) {
        try {
            // Revert decision to PENDING
            boolean decisionReverted = applicationDAO.updateHMDecision(Math.toIntExact(applicationId), "PENDING");

            if (decisionReverted) {
                // Add note about reversion
                ApplicantNote note = new ApplicantNote(
                        applicationId,
                        hiringManagerId,
                        "Hiring decision reverted to PENDING",
                        ApplicantNote.NoteType.INTERNAL
                );
                noteDAO.save(note);

                // Update candidate status if needed
                updateCandidateStatusByApplication(applicationId, "SENT_TO_HM");
            }

            return decisionReverted;
        } catch (Exception e) {
            throw new RuntimeException("Failed to revert hiring decision", e);
        }
    }

    private void updateCandidateStatusByApplication(Long applicationId, String status) {
        // This would require a new DAO method to find candidate by application ID
        // For now, this is a placeholder implementation
        try {
            // candidateDAO.findByApplicationId(applicationId).ifPresent(candidate -> {
            //     candidateDAO.updateStatus(candidate.getId(), status);
            // });
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to update candidate status: " + e.getMessage());
        }
    }

    private boolean isValidDecision(String decision) {
        return decision != null && decision.matches("SELECTED|REJECTED|PENDING|OFFER_EXTENDED");
    }
}