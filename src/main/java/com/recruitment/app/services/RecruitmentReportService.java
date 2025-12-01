package com.recruitment.app.services;

import com.recruitment.app.models.RecruitmentReport;

public interface RecruitmentReportService {
    RecruitmentReport generateReport(int jobId, int recruiterId);
    void exportReportAsPDF(RecruitmentReport report, String path) throws Exception;
}
