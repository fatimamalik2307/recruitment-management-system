package com.recruitment.app.reports;

import com.recruitment.app.models.RecruitmentReport;

public interface IReportGenerator {
    RecruitmentReport generate(int jobId, int recruiterId);
}
