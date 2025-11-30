package com.recruitment.app.services;

import com.recruitment.app.dao.AssessmentResultDAO;
import com.recruitment.app.models.AssessmentResult;

import java.util.List;

public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentResultDAO assessmentDAO;

    public AssessmentServiceImpl(AssessmentResultDAO assessmentDAO) {
        this.assessmentDAO = assessmentDAO;
    }

    @Override
    public AssessmentResult recordAssessment(AssessmentResult assessment) {
        // Optional: validate numeric fields, completeness
        return assessmentDAO.save(assessment);
    }

    @Override
    public List<AssessmentResult> getByShortlistId(int shortlistId) {
        return assessmentDAO.getByShortlistId(shortlistId);
    }
}
