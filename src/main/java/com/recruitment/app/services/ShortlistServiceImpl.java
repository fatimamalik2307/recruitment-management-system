package com.recruitment.app.services;

import com.recruitment.app.dao.*;
import com.recruitment.app.models.*;
import com.recruitment.app.utils.SessionManager;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShortlistServiceImpl implements ShortlistService {

    private final ShortlistDAO shortlistDAO;
    private final ShortlistingCriteriaDAO criteriaDAO;
    private final ApplicationDAO applicationDAO;
    private final FinalRankedCandidateDAO finalRankedCandidateDAO;
    private final JobDAO jobDAO;

    public ShortlistServiceImpl(ShortlistDAO shortlistDAO,
                                ShortlistingCriteriaDAO criteriaDAO,
                                ApplicationDAO applicationDAO,
                                FinalRankedCandidateDAO finalRankedCandidateDAO,
                                JobDAO jobDAO) {

        this.shortlistDAO = shortlistDAO;
        this.criteriaDAO = criteriaDAO;
        this.applicationDAO = applicationDAO;
        this.finalRankedCandidateDAO = finalRankedCandidateDAO;
        this.jobDAO = jobDAO;
    }
    @Override
    public List<JobPosting> getJobsEligibleForShortlisting() {
        int recruiterId = SessionManager.loggedInUser.getId();
        List<JobPosting> allJobs = jobDAO.getJobsByRecruiterId(recruiterId);

        return allJobs.stream()
                .filter(job -> {
                    // 1. Skip closed jobs
                    if (job.getDeadline() != null && job.getDeadline().isBefore(ChronoLocalDate.from(LocalDateTime.now()))) {
                        return false;
                    }

                    // 2. Skip if final ranking already exists
                    if (finalRankedCandidateDAO.existsForJob(job.getId())) {
                        List<FinalRankedCandidate> candidates = finalRankedCandidateDAO.getByJobId(job.getId());
                        boolean allHMReviewed = candidates.stream()
                                .allMatch(c -> c.getStatus().equalsIgnoreCase("HM_REVIEWED"));
                        return !allHMReviewed; // show only if not all HM_REVIEWED
                    }

                    // 3. Otherwise include job
                    return true;
                })
                .toList();
    }

    @Override
    public List<Shortlist> generateShortlist(int jobId) {

        // 1. Final ranking already created? → Do NOT regenerate shortlist
        if (finalRankedCandidateDAO.existsForJob(jobId)) {
            return null; // Controller will show alert: "Ranking already generated"
        }

        // 2. Shortlist already exists? → RETURN existing shortlist
        if (shortlistDAO.existsForJob(jobId)) {
            return shortlistDAO.getByJobId(jobId);
        }

        // 3. Get criteria for this job → Cannot generate shortlist without criteria
        ShortlistingCriteria criteria = criteriaDAO.getByJobId(jobId);
        if (criteria == null) return new ArrayList<>();

        // 4. Fetch all applications for this job
        List<Application> applications = applicationDAO.getApplicationsByJobId(jobId);

        List<Shortlist> result = new ArrayList<>();

        for (Application app : applications) {
            if (meetsCriteria(app, criteria)) {
                Shortlist s = new Shortlist();
                s.setCriteriaId(criteria.getId());
                s.setApplicationId(app.getId());
                s.setShortlistedAt(LocalDateTime.now());

                shortlistDAO.save(s);
                result.add(s);
            }
        }

        // 5. Close job after generating shortlist
        jobDAO.closeJob(jobId);

        return result;
    }

    @Override
    public List<Shortlist> getShortlistByJob(int jobId) {
        return shortlistDAO.getByJobId(jobId);
    }

    @Override
    public Shortlist addToShortlist(int criteriaId, int applicationId) {
        Shortlist s = new Shortlist();
        s.setCriteriaId(criteriaId);
        s.setApplicationId(applicationId);
        s.setShortlistedAt(LocalDateTime.now());
        return shortlistDAO.save(s);
    }

    private boolean meetsCriteria(Application app, ShortlistingCriteria criteria) {

        // 1. Qualification
        if (criteria.getRequiredQualification() != null &&
                !criteria.getRequiredQualification().isEmpty() &&
                !criteria.getRequiredQualification().equalsIgnoreCase(app.getQualification())) {
            return false;
        }

        // 2. Experience check
        if (criteria.getMinExperience() != null) {
            try {
                int applicantExp = Integer.parseInt(app.getExperience().split(" ")[0]);
                if (applicantExp < criteria.getMinExperience()) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        // 3. Required skills → search in cover letter
        if (criteria.getRequiredSkills() != null && !criteria.getRequiredSkills().isEmpty()) {
            String[] keywords = criteria.getRequiredSkills().split(",\\s*");
            for (String keyword : keywords) {
                if (!app.getCoverLetter().toLowerCase().contains(keyword.toLowerCase())) {
                    return false;
                }
            }
        }

        return true;
    }
}
