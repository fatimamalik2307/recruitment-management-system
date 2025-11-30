package com.recruitment.app.dao;

import com.recruitment.app.models.AssessmentResult;
import java.util.List;

public interface AssessmentResultDAO {
    AssessmentResult save(AssessmentResult assessment);
    List<AssessmentResult> getByShortlistId(int shortlistId);
    List<AssessmentResult> getByJobId(int jobId);
    void delete(int id);
}
