package com.recruitment.app.dao;

import com.recruitment.app.models.JobDescription;
import java.util.List;

public interface JobDescriptionDAO {
    void save(JobDescription desc);
    List<JobDescription> getByRecruiter(int recruiterId);
}
