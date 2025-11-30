package com.recruitment.app.dao;

import com.recruitment.app.models.Application;
import java.util.List;

public interface ApplicationDAO {
    // Existing methods
    List<Application> getApplicationsByJobId(int jobId);
    String getApplicantFullName(int userId);
    Application getById(int applicationId);
    String getApplicantPhone(int userId);

    // Updated HM methods - using int and existing status field
    boolean updateHMDecision(int applicationId, String decision);
    List<Application> findByJobAndHmDecision(int jobId, String decision);
    int countByJobAndDecision(int jobId, String decision);
}