package com.recruitment.app.services;

import com.recruitment.app.dao.JobDescriptionDAO;
import com.recruitment.app.models.JobDescription;

public class JobDescriptionServiceImpl implements JobDescriptionService {

    private final JobDescriptionDAO dao;

    public JobDescriptionServiceImpl(JobDescriptionDAO dao) {
        this.dao = dao;
    }

    @Override
    public boolean saveDescription(JobDescription desc) {
        try {
            dao.save(desc);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
