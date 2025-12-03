package com.recruitment.app.services;

import com.recruitment.app.models.Application;
import java.util.List;

public interface ApplicationService {

    void submit(Application application);

    Application getById(int id);

    List<Application> getByJob(int jobId);
    String getJobTitle(int jobId);

    List<Application> getByUser(int userId);

    boolean updateHMDecision(int applicationId, String decision);

    int countByDecision(int jobId, String decision);
}
