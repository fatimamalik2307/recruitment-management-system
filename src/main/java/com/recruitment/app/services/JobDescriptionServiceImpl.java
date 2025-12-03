package com.recruitment.app.services;

import com.recruitment.app.dao.JobDescriptionDAO;
import com.recruitment.app.models.JobDescription;

import java.util.List;

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

    @Override
    public List<JobDescription> getByRecruiter(int recruiterId) {
        return dao.getByRecruiter(recruiterId);
    }
}
