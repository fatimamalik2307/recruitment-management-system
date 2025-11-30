package com.recruitment.app.services;

import com.recruitment.app.models.AssessmentResult;
import java.util.List;

public interface AssessmentService {
    AssessmentResult recordAssessment(AssessmentResult assessment);
    List<AssessmentResult> getByShortlistId(int shortlistId);
}
