package com.recruitment.app.services;

import com.recruitment.app.dao.ApplicationDAO;
import com.recruitment.app.models.Application;

import java.util.List;

public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationDAO applicationDAO;

    public ApplicationServiceImpl(ApplicationDAO dao) {
        this.applicationDAO = dao;
    }

    @Override
    public void submit(Application application) {
        applicationDAO.save(application);
    }

    @Override
    public Application getById(int id) {
        return applicationDAO.getById(id);
    }

    @Override
    public List<Application> getByJob(int jobId) {
        return applicationDAO.getApplicationsByJobId(jobId);
    }

    @Override
    public List<Application> getByUser(int userId) {
        return applicationDAO.getApplicationsByUserId(userId);
    }

    @Override
    public boolean updateHMDecision(int applicationId, String decision) {
        return applicationDAO.updateHMDecision(applicationId, decision);
    }

    @Override
    public int countByDecision(int jobId, String decision) {
        return applicationDAO.countByJobAndDecision(jobId, decision);
    }
}
