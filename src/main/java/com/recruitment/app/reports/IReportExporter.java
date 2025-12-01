package com.recruitment.app.reports;

import com.recruitment.app.models.RecruitmentReport;

public interface IReportExporter {
    void export(RecruitmentReport report, String filepath) throws Exception;
}
