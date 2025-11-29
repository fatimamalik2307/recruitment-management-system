package com.recruitment.app.dao;

import com.recruitment.app.models.Application;
import java.util.List;

public interface ApplicationDAO {
    List<Application> getApplicationsByJobId(int jobId);
    String getApplicantFullName(int userId);
}
