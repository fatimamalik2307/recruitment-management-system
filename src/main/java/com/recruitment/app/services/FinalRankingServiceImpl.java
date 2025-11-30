package com.recruitment.app.services;

import com.recruitment.app.dao.*;
import com.recruitment.app.models.*;

import com.recruitment.app.utils.SessionManager;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FinalRankingServiceImpl implements FinalRankingService {

    private final FinalRankedCandidateDAO candidateDAO;
    private final FinalRankingCriteriaDAO criteriaDAO;
    private final AssessmentResultDAO assessmentDAO;
    private final JobDAO jobDAO;
    private final ShortlistDAO shortlistDAO;
    private final UserDAO userDAO;

    private final Set<Integer> readyForHMJobs = new HashSet<>();

    public FinalRankingServiceImpl(FinalRankedCandidateDAO candidateDAO,
                                   FinalRankingCriteriaDAO criteriaDAO,
                                   AssessmentResultDAO assessmentDAO,
                                   JobDAO jobDAO,
                                   ShortlistDAO shortlistDAO,
                                   UserDAO userDAO) {
        this.candidateDAO = candidateDAO;
        this.criteriaDAO = criteriaDAO;
        this.assessmentDAO = assessmentDAO;
        this.jobDAO = jobDAO;
        this.shortlistDAO = shortlistDAO;
        this.userDAO = userDAO;
    }

    /** ==========================
     * Jobs
     * ========================== */
    public List<JobPosting> getAllJobsForRecruiter() {
        int id = SessionManager.loggedInUser.getId();
        return jobDAO.getJobsByRecruiterId(id);
    }

    /** ==========================
     * Ranking Criteria
     * ========================== */
    @Override
    public void setRankingCriteria(FinalRankingCriteria criteria) {
        FinalRankingCriteria existing = criteriaDAO.getByJobId(criteria.getJobId());
        if (existing != null) {
            criteria.setId(existing.getId());
            criteriaDAO.update(criteria);
        } else {
            criteriaDAO.save(criteria);
        }
    }

    @Override
    public FinalRankingCriteria getRankingCriteria(int jobId) {
        return criteriaDAO.getByJobId(jobId);
    }

    /** ==========================
     * Generate final ranking
     * ========================== */
    @Override
    public List<FinalRankedCandidate> generateFinalRanking(JobPosting job,
                                                           List<AssessmentResult> assessments,
                                                           List<Shortlist> shortlists,
                                                           double technicalWeight,
                                                           double hrWeight) {
        if (assessments.isEmpty() || shortlists.isEmpty()) return Collections.emptyList();

        // Map assessments by shortlistId for quick lookup
        Map<Integer, AssessmentResult> assessmentMap = new HashMap<>();
        for (AssessmentResult ar : assessments) {
            assessmentMap.put(ar.getShortlistId(), ar);
        }

        List<FinalRankedCandidate> finalList = new ArrayList<>();
        int rank = 1; // start ranking

        for (Shortlist s : shortlists) {
            AssessmentResult a = assessmentMap.get(s.getId()); // match by shortlistId
            if (a == null) continue; // skip if assessment missing

            // Calculate composite score using weights
            double composite = a.getTechnicalScore() * technicalWeight + a.getHrScore() * hrWeight;

            FinalRankedCandidate candidate = new FinalRankedCandidate();
            candidate.setApplicationId(s.getApplicationId()); // use the actual applicationId
            candidate.setJobId(job.getId());

            // Fetch applicant name from Application/User DAO
            String applicantName = "Unknown";
            var user = userDAO.getByApplicationId(s.getApplicationId()); // fetch user linked to application
            if (user != null) applicantName = user.getFullName();
            candidate.setApplicantName(applicantName);

            candidate.setCompositeScore(composite);
            candidate.setGeneratedAt(LocalDateTime.now());
            candidate.setStatus("Pending");
            candidate.setRank(rank++); // assign rank incrementally

            finalList.add(candidate);
        }



        finalList.sort((c1, c2) -> Double.compare(c2.getCompositeScore(), c1.getCompositeScore()));
        for (int i = 0; i < finalList.size(); i++) {
            finalList.get(i).setRank(i + 1);
        }

        saveFinalRanking(finalList);
        flagListReady(job.getId());

        return finalList;
    }

    /** ==========================
     * Convenience method for generating by jobId (fetch assessments & shortlist automatically)
     * ========================== */
    public List<FinalRankedCandidate> generateFinalRankingForJob(int jobId) {
        JobPosting job = jobDAO.getJobById(jobId);
        if (job == null) throw new IllegalStateException("Job not found");

        List<AssessmentResult> assessments = assessmentDAO.getByJobId(jobId);
        List<Shortlist> shortlists = shortlistDAO.getByJobId(jobId);

        FinalRankingCriteria criteria = criteriaDAO.getByJobId(jobId);
        if (criteria == null) {
            double[] weights = getCriteriaFromUser();
            criteria = new FinalRankingCriteria(jobId, weights[0], weights[1], 1.0);
            criteriaDAO.save(criteria);
        }

        return generateFinalRanking(job, assessments, shortlists, criteria.getTechnicalWeight(), criteria.getHrWeight());
    }

    private double[] getCriteriaFromUser() {
        final double[] weights = new double[2];
        Runnable dialogTask = () -> {
            TextInputDialog techDialog = new TextInputDialog("0.7");
            techDialog.setTitle("Final Ranking Criteria");
            techDialog.setHeaderText("Enter Technical Weight (0.0 - 1.0)");
            Optional<String> techInput = techDialog.showAndWait();

            TextInputDialog hrDialog = new TextInputDialog("0.3");
            hrDialog.setTitle("Final Ranking Criteria");
            hrDialog.setHeaderText("Enter HR Weight (0.0 - 1.0)");
            Optional<String> hrInput = hrDialog.showAndWait();

            if (techInput.isEmpty() || hrInput.isEmpty()) {
                throw new IllegalStateException("Final ranking criteria not set.");
            }

            try {
                weights[0] = Double.parseDouble(techInput.get());
                weights[1] = Double.parseDouble(hrInput.get());
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid input for weights.");
            }
        };

        if (Platform.isFxApplicationThread()) {
            dialogTask.run();
        } else {
            try {
                Platform.runLater(dialogTask);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return weights;
    }

    /** ==========================
     * Save final ranking
     * ========================== */
    @Override
    public void saveFinalRanking(List<FinalRankedCandidate> finalList) {
        for (FinalRankedCandidate candidate : finalList){
            candidateDAO.save(candidate);
            } // Make sure DAO has saveAll()
    }

    /** ==========================
     * Flag list ready
     * ========================== */
    @Override
    public void flagListReady(int jobId) {
        // Update in-memory flag (optional, still useful)
        readyForHMJobs.add(jobId);

        // Update all candidates for this job
        List<FinalRankedCandidate> candidates = candidateDAO.getByJobId(jobId);
        for (FinalRankedCandidate candidate : candidates) {
            candidateDAO.updateStatusAndNotes(candidate.getId(), "Ready for HM", candidate.getHmNotes());
        }
    }

    /** ==========================
     * Get ranking for job
     * ========================== */
    @Override
    public List<FinalRankedCandidate> getFinalRankingByJob(int jobId) {
        return candidateDAO.getByJobId(jobId);
    }

    /** ==========================
     * Update status & HM notes
     * ========================== */
    @Override
    public void updateStatusAndNotes(int candidateId, String status, String hmNotes) {
        candidateDAO.updateStatusAndNotes(candidateId, status, hmNotes);
    }

    /** ==========================
     * Check if ready for HM
     * ========================== */
    public boolean isListReadyForHM(int jobId) {
        return readyForHMJobs.contains(jobId);
    }
}
