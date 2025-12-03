package com.recruitment.app.reports;

import com.recruitment.app.dao.RecruitmentReportDAO;
import com.recruitment.app.dao.RecruitmentReportDAOImpl;
import com.recruitment.app.models.RecruitmentReport;

public class DatabaseReportGenerator implements IReportGenerator {

    private final RecruitmentReportDAO reportDAO = new RecruitmentReportDAOImpl();

    @Override
    public RecruitmentReport generate(int jobId, int recruiterId) {
        // 1. Check if a report already exists
        RecruitmentReport existingReport = reportDAO.getReportByJobId(jobId);
        if (existingReport != null) {
            return existingReport;
        }

        // 2. Generate a new report if none exists
        return reportDAO.generateReport(jobId, recruiterId);
    }
}
