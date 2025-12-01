package com.recruitment.app.reports;

import com.recruitment.app.models.RecruitmentReport;

public class ReportBuilder {

    private IReportGenerator generator;
    private IReportExporter exporter;

    public ReportBuilder setGenerator(IReportGenerator generator) {
        this.generator = generator;
        return this;
    }

    public ReportBuilder setExporter(IReportExporter exporter) {
        this.exporter = exporter;
        return this;
    }

    public RecruitmentReport buildReport(int jobId, int recruiterId) {
        return generator.generate(jobId, recruiterId);
    }

    public void exportReport(RecruitmentReport report, String path) throws Exception {
        exporter.export(report, path);
    }
}
