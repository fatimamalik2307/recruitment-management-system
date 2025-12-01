package com.recruitment.app.services;

import com.recruitment.app.models.RecruitmentReport;
import com.recruitment.app.reports.*;

public class RecruitmentReportServiceImpl implements RecruitmentReportService {

    private final ReportBuilder reportBuilder = new ReportBuilder()
            .setGenerator(new DatabaseReportGenerator())
            .setExporter(new PDFReportExporter());

    @Override
    public RecruitmentReport generateReport(int jobId, int recruiterId) {
        return reportBuilder.buildReport(jobId, recruiterId);
    }

    @Override
    public void exportReportAsPDF(RecruitmentReport report, String path) throws Exception {
        reportBuilder.exportReport(report, path);
    }
}
