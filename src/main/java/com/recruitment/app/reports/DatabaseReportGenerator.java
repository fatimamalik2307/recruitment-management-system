package com.recruitment.app.reports;

import com.recruitment.app.dao.RecruitmentReportDAO;
import com.recruitment.app.dao.RecruitmentReportDAOImpl;
import com.recruitment.app.models.RecruitmentReport;

public class DatabaseReportGenerator implements IReportGenerator {

    private final RecruitmentReportDAO reportDAO = new RecruitmentReportDAOImpl();

    @Override
    public RecruitmentReport generate(int jobId, int recruiterId) {
        return reportDAO.generateReport(jobId, recruiterId);
    }
}
