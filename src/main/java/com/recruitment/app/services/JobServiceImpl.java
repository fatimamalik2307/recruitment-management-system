package com.recruitment.app.services;

import com.recruitment.app.dao.CompanyDAO;
import com.recruitment.app.dao.JobDAO;
import com.recruitment.app.dao.UserDAO;
import com.recruitment.app.models.Company;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.User;

import java.util.List;

public class JobServiceImpl implements JobService {
    private final JobDAO jobDAO;
    private final UserDAO userDAO;

    public JobServiceImpl(JobDAO jobDAO,  UserDAO userDAO) {
        this.jobDAO = jobDAO;
        this.userDAO = userDAO;
    }
    @Override
    public User getHiringManagerForRecruiter(int recruiterId) {
        return userDAO.findHiringManagerByRecruiterId(recruiterId);
    }

    @Override
    public List<JobPosting>  getAllJobs(){
        return jobDAO.getAllJobs();
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



    @Override
    public JobPosting getJobById(int jobId) {
        return jobDAO.getJobById(jobId);
    }

    @Override
    public List<JobPosting> getAllJobsForRecruiter(int recruiterId) {
        return jobDAO.getJobsByRecruiterId(recruiterId);
    }
}