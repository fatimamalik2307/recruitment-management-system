package com.recruitment.app.services;

import com.recruitment.app.models.Application;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.ShortlistingCriteria;
import java.util.List;

public interface RecruiterService {
    List<Application> getApplicationsForJob(int jobId);
    void saveShortlistingCriteria(ShortlistingCriteria criteria);
    ShortlistingCriteria getShortlistingCriteria(int jobId);
    List<JobPosting> getJobsByRecruiter(int recruiterId);
    String getApplicantName(int userId);
}
