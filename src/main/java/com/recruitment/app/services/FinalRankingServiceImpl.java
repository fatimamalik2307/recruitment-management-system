package com.recruitment.app.services;

import com.recruitment.app.dao.*;
import com.recruitment.app.models.*;
import com.recruitment.app.utils.SessionManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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

    @Override
    public boolean existsForJob(int jobId) {
        return candidateDAO.existsForJob(jobId);
    }

    public List<JobPosting> getAllJobsForRecruiter() {
        int id = SessionManager.loggedInUser.getId();
        return jobDAO.getJobsByRecruiterId(id);
    }

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

    @Override
    public List<Application> getFinalRankingApplicationsByJob(int jobId) {
        return candidateDAO.getFinalRankingApplicationsByJob(jobId);
    }

    @Override
    public List<FinalRankedCandidate> generateFinalRanking(JobPosting job,
                                                           List<AssessmentResult> assessments,
                                                           List<Shortlist> shortlists,
                                                           double technicalWeight,
                                                           double hrWeight) {
        if (assessments.isEmpty() || shortlists.isEmpty()) {
            // No alerts here; controller will handle empty list
            return Collections.emptyList();
        }

        Map<Integer, AssessmentResult> assessmentMap = new HashMap<>();
        for (AssessmentResult ar : assessments) {
            assessmentMap.put(ar.getShortlistId(), ar);
        }

        List<FinalRankedCandidate> finalList = new ArrayList<>();
        int rank = 1;

        for (Shortlist s : shortlists) {
            AssessmentResult a = assessmentMap.get(s.getId());
            if (a == null) continue;

            double composite = a.getTechnicalScore() * technicalWeight + a.getHrScore() * hrWeight;

            FinalRankedCandidate candidate = new FinalRankedCandidate();
            candidate.setApplicationId(s.getApplicationId());
            candidate.setJobId(job.getId());

            var user = userDAO.getByApplicationId(s.getApplicationId());
            candidate.setApplicantName(user != null ? user.getFullName() : "Unknown");

            candidate.setCompositeScore(composite);
            candidate.setGeneratedAt(LocalDateTime.now());
            candidate.setStatus("Pending");
            candidate.setRank(rank++);
            finalList.add(candidate);
        }

        finalList.sort((c1, c2) -> Double.compare(c2.getCompositeScore(), c1.getCompositeScore()));
        for (int i = 0; i < finalList.size(); i++) finalList.get(i).setRank(i + 1);

        saveFinalRanking(finalList);
        flagListReady(job.getId());

        return finalList;
    }

    @Override
    public List<FinalRankedCandidate> generateFinalRankingForJob(int jobId) {
        JobPosting job = jobDAO.getJobById(jobId);
        if (job == null) return Collections.emptyList();

        if (candidateDAO.existsForJob(jobId)) {
            List<FinalRankedCandidate> existing = candidateDAO.getByJobId(jobId);
            return existing;
        }

        List<Shortlist> shortlists = shortlistDAO.getByJobId(jobId);
        if (shortlists.isEmpty()) return Collections.emptyList();

        List<AssessmentResult> assessments = assessmentDAO.getByJobId(jobId);
        if (assessments.isEmpty() || assessments.size() < shortlists.size()) return Collections.emptyList();

        FinalRankingCriteria criteria = criteriaDAO.getByJobId(jobId);
        if (criteria == null) {
            double[] weights = getCriteriaFromUser();
            if (weights == null) return Collections.emptyList();
            criteria = new FinalRankingCriteria(jobId, weights[0], weights[1], 1.0);
            criteriaDAO.save(criteria);
        }

        return generateFinalRanking(job, assessments, shortlists, criteria.getTechnicalWeight(), criteria.getHrWeight());
    }

    @Override
    public List<JobPosting> getJobsEligibleForFinalRanking() {
        int recruiterId = SessionManager.loggedInUser.getId();
        List<JobPosting> allJobs = jobDAO.getJobsByRecruiterId(recruiterId);

        LocalDate today = LocalDate.now();

        return allJobs.stream()
                // Only jobs whose deadline is today or in the future
                .filter(job -> job.getDeadline() != null && !job.getDeadline().isBefore(today))
                .filter(job -> {
                    // If final ranking exists, only include if not all candidates are sent to HM
                    if (candidateDAO.existsForJob(job.getId())) {
                        List<FinalRankedCandidate> candidates = candidateDAO.getByJobId(job.getId());

                        // Job is eligible if at least one candidate is NOT yet sent to HM
                        return candidates.stream()
                                .anyMatch(c -> !c.getStatus().equalsIgnoreCase("HM_REVIEWED"));
                    }
                    return true; // No candidates yet, include the job
                })
                .toList();
    }



    @Override
    public double[] getCriteriaFromUser() {
        final double[] weights = new double[2];
        final boolean[] cancelled = {false};

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
                cancelled[0] = true;
                showAlert("Final ranking criteria not set.", Alert.AlertType.WARNING);
                return;
            }

            try {
                weights[0] = Double.parseDouble(techInput.get());
                weights[1] = Double.parseDouble(hrInput.get());
            } catch (NumberFormatException e) {
                cancelled[0] = true;
                showAlert("Invalid input for weights.", Alert.AlertType.WARNING);
            }
        };

        if (Platform.isFxApplicationThread()) {
            dialogTask.run();
        } else {
            try {
                Platform.runLater(dialogTask);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return cancelled[0] ? null : weights;
    }

    @Override
    public void saveFinalRanking(List<FinalRankedCandidate> finalList) {
        for (FinalRankedCandidate candidate : finalList) candidateDAO.save(candidate);
    }

    @Override
    public void flagListReady(int jobId) {
        readyForHMJobs.add(jobId);
        List<FinalRankedCandidate> candidates = candidateDAO.getByJobId(jobId);
        for (FinalRankedCandidate candidate : candidates) {
            candidateDAO.updateStatusAndNotes(candidate.getId(), "Ready for HM", candidate.getHmNotes());
        }
    }

    @Override
    public List<FinalRankedCandidate> getFinalRankingByJob(int jobId) {
        return candidateDAO.getByJobId(jobId);
    }

    @Override
    public void updateStatusAndNotes(int candidateId, String status, String hmNotes) {
        candidateDAO.updateStatusAndNotes(candidateId, status, hmNotes);
    }

    public boolean isListReadyForHM(int jobId) {
        return readyForHMJobs.contains(jobId);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle("Alert");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}

