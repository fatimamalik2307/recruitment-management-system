package com.recruitment.app.services;

import com.recruitment.app.dao.ApplicationDAO;
import com.recruitment.app.dao.JobDAO;
import com.recruitment.app.dao.ShortlistingCriteriaDAO;
import com.recruitment.app.models.Application;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.ShortlistingCriteria;

import java.util.List;

public class RecruiterServiceImpl implements RecruiterService {

    private final JobDAO jobDAO;
    private final ApplicationDAO applicationDAO;
    private final ShortlistingCriteriaDAO criteriaDAO;

    public RecruiterServiceImpl(JobDAO jobDAO, ApplicationDAO applicationDAO, ShortlistingCriteriaDAO criteriaDAO) {
        this.jobDAO = jobDAO;
        this.applicationDAO = applicationDAO;
        this.criteriaDAO = criteriaDAO;
    }

    @Override
    public List<Application> getApplicationsForJob(int jobId) {
        return applicationDAO.getApplicationsByJobId(jobId);
    }

    @Override
    public void saveShortlistingCriteria(ShortlistingCriteria criteria) {
        criteriaDAO.save(criteria);
    }

    @Override
    public ShortlistingCriteria getShortlistingCriteria(int jobId) {
        return criteriaDAO.getByJobId(jobId);
    }

    @Override
    public List<JobPosting> getJobsByRecruiter(int recruiterId) {
        return jobDAO.getJobsByRecruiterId(recruiterId);
    }

    @Override
    public String getApplicantName(int userId) {
        return applicationDAO.getApplicantFullName(userId);
    }
    @Override
    public Application getApplicationById(int applicationId) {
        return applicationDAO.getById(applicationId); // make sure this DAO method exists
    }


    @Override
    public String getApplicantPhone(int userId) {
        return applicationDAO.getApplicantPhone(userId);
    }
}
