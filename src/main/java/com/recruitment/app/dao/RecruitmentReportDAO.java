package com.recruitment.app.dao;

import com.recruitment.app.models.RecruitmentReport;

public interface RecruitmentReportDAO {

    RecruitmentReport generateReport(int jobId, int recruiterId);

    RecruitmentReport getReportByJobId(int jobId);
}
