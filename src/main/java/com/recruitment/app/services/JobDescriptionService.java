package com.recruitment.app.services;

import com.recruitment.app.models.JobDescription;

import java.util.List;

public interface JobDescriptionService {
    boolean saveDescription(JobDescription desc);
    List<JobDescription> getByRecruiter(int recruiterId);
}
