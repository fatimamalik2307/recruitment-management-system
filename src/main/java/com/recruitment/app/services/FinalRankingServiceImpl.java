package com.recruitment.app.services;

import com.recruitment.app.dao.*;
import com.recruitment.app.models.*;
import com.recruitment.app.utils.SessionManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        System.out.println("DEBUG(FinalRankingServiceImpl): Service created");
        System.out.println("DEBUG(FinalRankingServiceImpl): userDAO = " + userDAO);
        System.out.println("DEBUG(FinalRankingServiceImpl): jobDAO = " + jobDAO);
        System.out.println("DEBUG(FinalRankingServiceImpl): shortlistDAO = " + shortlistDAO);
        System.out.println("DEBUG(FinalRankingServiceImpl): assessmentDAO = " + assessmentDAO);
    }

    @Override
    public boolean existsForJob(int jobId) {
        return candidateDAO.existsForJob(jobId);
    }

    public List<JobPosting> getAllJobsForRecruiter() {
        int id = SessionManager.loggedInUser.getId();
        System.out.println("DEBUG: getAllJobsForRecruiter -> recruiterId=" + id);

        List<JobPosting> jobs = jobDAO.getJobsByRecruiterId(id);
        System.out.println("DEBUG: Jobs returned -> " + jobs.size());
        return jobs;
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
        System.out.println("DEBUG(generateFinalRanking): Called");
        System.out.println("DEBUG(generateFinalRanking): Job = " + job);
        System.out.println("DEBUG(generateFinalRanking): Assessments size = " + assessments.size());
        System.out.println("DEBUG(generateFinalRanking): Shortlists size = " + shortlists.size());
        System.out.println("DEBUG(generateFinalRanking): technicalWeight=" + technicalWeight + " hrWeight=" + hrWeight);

        if (assessments.isEmpty() || shortlists.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, AssessmentResult> assessmentMap = new HashMap<>();
        for (AssessmentResult ar : assessments) {
            System.out.println("DEBUG: Mapping assessment shortlistId=" + ar.getShortlistId());
            assessmentMap.put(ar.getShortlistId(), ar);
        }

        List<FinalRankedCandidate> finalList = new ArrayList<>();
        int rank = 1;

        for (Shortlist s : shortlists) {
            System.out.println("DEBUG: Processing shortlist ID=" + s.getId() + " applicationId=" + s.getApplicationId());
            AssessmentResult a = assessmentMap.get(s.getId());
            System.out.println("DEBUG: Assessment found = " + (a != null));
            if (a == null) continue;

            double composite = a.getTechnicalScore() * technicalWeight + a.getHrScore() * hrWeight;
            System.out.println("DEBUG: Composite score = " + composite);

            FinalRankedCandidate candidate = new FinalRankedCandidate();
            candidate.setApplicationId(s.getApplicationId());
            candidate.setJobId(job.getId());

            System.out.println("DEBUG: Calling userDAO.getByApplicationId(" + s.getApplicationId() + ")");
            var user = userDAO.getByApplicationId(s.getApplicationId());
            System.out.println("DEBUG: userDAO returned -> " + user);

            candidate.setApplicantName(user != null ? user.getFullName() : "Unknown");
            candidate.setCompositeScore(composite);
            candidate.setGeneratedAt(LocalDateTime.now());
            candidate.setStatus("Pending");
            candidate.setRank(rank++);

            System.out.println("DEBUG: Adding candidate -> " + candidate.getApplicantName());
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
        System.out.println("DEBUG(generateFinalRankingForJob): jobId=" + jobId);

        JobPosting job = jobDAO.getJobById(jobId);
        System.out.println("DEBUG: jobDAO.getJobById returned -> " + job);
        if (job == null) return Collections.emptyList();

        if (candidateDAO.existsForJob(jobId)) {
            System.out.println("DEBUG: Final ranking already exists. Fetching...");
            List<FinalRankedCandidate> existing = candidateDAO.getByJobId(jobId);
            System.out.println("DEBUG: Existing size -> " + existing.size());
            for (FinalRankedCandidate c : existing) {
                var user = userDAO.getByApplicationId(c.getApplicationId());
                c.setApplicantName(user != null ? user.getFullName() : "Unknown");
                System.out.println("DEBUG: Populating existing candidate name -> " + c.getApplicantName());
            }
            return existing;
        }

        List<Shortlist> shortlists = shortlistDAO.getByJobId(jobId);
        System.out.println("DEBUG: shortlistDAO.getByJobId size -> " + shortlists.size());
        if (shortlists.isEmpty()) return Collections.emptyList();

        List<AssessmentResult> assessments = assessmentDAO.getByJobId(jobId);
        System.out.println("DEBUG: assessmentDAO.getByJobId size -> " + assessments.size());
        if (assessments.isEmpty() || assessments.size() < shortlists.size()) return Collections.emptyList();

        FinalRankingCriteria criteria = criteriaDAO.getByJobId(jobId);
        System.out.println("DEBUG: criteriaDAO.getByJobId returned -> " + criteria);
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
                .filter(job -> job.getDeadline() != null && !job.getDeadline().isBefore(today))
                .filter(job -> {
                    if (candidateDAO.existsForJob(job.getId())) {
                        List<FinalRankedCandidate> candidates = candidateDAO.getByJobId(job.getId());
                        return candidates.stream()
                                .anyMatch(c -> !c.getStatus().equalsIgnoreCase("HM_REVIEWED"));
                    }
                    return true;
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
        System.out.println("DEBUG: Saving final ranking list size=" + finalList.size());
        for (FinalRankedCandidate candidate : finalList) {
            System.out.println("DEBUG: Saving candidate -> " + candidate);
            candidateDAO.save(candidate);
        }
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
