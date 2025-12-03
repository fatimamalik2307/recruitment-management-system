package com.recruitment.app.dao;

import com.recruitment.app.models.Shortlist;
import java.util.List;

public interface ShortlistDAO {
    Shortlist save(Shortlist shortlist);

    boolean existsForJob(int jobId);

    List<Shortlist> getByJobId(int jobId);
    Shortlist getById(int id);
    void delete(int id);
    String getApplicantNameByApplicationId(int applicationId);
}
