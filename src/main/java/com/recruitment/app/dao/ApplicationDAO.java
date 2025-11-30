package com.recruitment.app.dao;

import com.recruitment.app.models.Application;
import java.util.List;

public interface ApplicationDAO {
    List<Application> getApplicationsByJobId(int jobId);
    String getApplicantFullName(int userId);
    String getApplicantPhone(int userId);
    Application getById(int applicationId);
    void save(Application application);
    List<Application> getApplicationsByUserId(int userId);
    String getJobTitle(int jobId);
}
