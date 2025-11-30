package com.recruitment.app.services;

import com.recruitment.app.dao.ApplicationDAO;
import com.recruitment.app.dao.ShortlistDAO;
import com.recruitment.app.dao.ShortlistingCriteriaDAO;
import com.recruitment.app.models.Application;
import com.recruitment.app.models.Shortlist;
import com.recruitment.app.models.ShortlistingCriteria;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShortlistServiceImpl implements ShortlistService {

    private final ShortlistDAO shortlistDAO;
    private final ShortlistingCriteriaDAO criteriaDAO;
    private final ApplicationDAO applicationDAO;

    public ShortlistServiceImpl(ShortlistDAO shortlistDAO, ShortlistingCriteriaDAO criteriaDAO, ApplicationDAO applicationDAO) {
        this.shortlistDAO = shortlistDAO;
        this.criteriaDAO = criteriaDAO;
        this.applicationDAO = applicationDAO;
    }

    @Override
    public List<Shortlist> generateShortlist(int jobId) {
        ShortlistingCriteria criteria = criteriaDAO.getByJobId(jobId);
        if (criteria == null) return new ArrayList<>();

        List<Application> applications = applicationDAO.getApplicationsByJobId(jobId);
        List<Shortlist> shortlistList = new ArrayList<>();

        for (Application app : applications) {
            if (meetsCriteria(app, criteria)) {
                Shortlist s = new Shortlist();
                s.setApplicationId(app.getId());
                s.setCriteriaId(criteria.getId());
                s.setShortlistedAt(LocalDateTime.now());
                shortlistDAO.save(s);
                shortlistList.add(s);
            }
        }
        return shortlistList;
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
        // 1. Qualification match
        if (criteria.getRequiredQualification() != null &&
                !criteria.getRequiredQualification().isEmpty() &&
                !criteria.getRequiredQualification().equalsIgnoreCase(app.getQualification())) {
            return false;
        }

        // 2. Minimum experience
        if (criteria.getMinExperience() != null) {
            try {
                // Assuming experience stored as "3 years", "2 years" etc.
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
