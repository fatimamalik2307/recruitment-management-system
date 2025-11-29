package com.recruitment.app.services;

import com.recruitment.app.dao.JobDAO;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.JobService;

/**
 * Implements business logic for job posting
 */
public class JobServiceImpl implements JobService {

    private final JobDAO jobDAO;

    public JobServiceImpl(JobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }

    @Override
    public boolean createJob(JobPosting job) {
        try {
            jobDAO.addJob(job);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
