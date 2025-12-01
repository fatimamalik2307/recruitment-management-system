package com.recruitment.app.dao;

import com.recruitment.app.models.Application;
import java.util.List;

public interface ApplicationDAO {

    // Core CRUD
    void save(Application application);
    Application getById(int applicationId);

    // Queries
    List<Application> getApplicationsByJobId(int jobId);
    List<Application> getApplicationsByUserId(int userId);

    // User Info
    String getApplicantFullName(int userId);
    String getApplicantPhone(int userId);

    // Job Info
    String getJobTitle(int jobId);

    // HM Decision (Your additions)
    boolean updateHMDecision(int applicationId, String decision);
    List<Application> findByJobAndHmDecision(int jobId, String decision);
    int countByJobAndDecision(int jobId, String decision);
}
