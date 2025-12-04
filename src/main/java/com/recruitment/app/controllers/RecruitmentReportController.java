package com.recruitment.app.controllers;

import com.recruitment.app.models.RecruitmentReport;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.RecruitmentReportService;
import com.recruitment.app.services.JobService;
import com.recruitment.app.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class RecruitmentReportController {

    @FXML private ComboBox<JobPosting> jobComboBox;
    @FXML private Label jobTitleLabel;
    @FXML private Label departmentLabel;
    @FXML private Label postedDateLabel;
    @FXML private Label totalAppsLabel;
    @FXML private Label shortlistedLabel;
    @FXML private Label generatedDateLabel;

    private RecruitmentReport report;

    private RecruitmentReportService reportService;
    private JobService jobService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public RecruitmentReportController() { }

    // ---------- SERVICE INJECTION ----------
    public void setServices(RecruitmentReportService reportService, JobService jobService) {
        this.reportService = reportService;
        this.jobService = jobService;

        loadJobsForRecruiter();
    }

    // ---------- LOAD JOBS AFTER INJECTION ----------
    private void loadJobsForRecruiter() {
        if (jobService == null) {
            showError("JobService not initialized!");
            return;
        }

        int recruiterId = SessionManager.loggedInUser.getId();

        try {
            List<JobPosting> jobs = jobService.getAllJobsForRecruiter(recruiterId);

            if (jobs == null || jobs.isEmpty()) {
                showError("No jobs found for this recruiter.");
                return;
            }

            jobComboBox.setItems(FXCollections.observableArrayList(jobs));

        } catch (Exception e) {
            showError("Failed to load jobs: " + e.getMessage());
        }
    }

    @FXML
    public void onGenerateReport() {

        if (reportService == null) {
            showError("Service not initialized!");
            return;
        }

        if (jobComboBox.getItems().isEmpty()) {
            showError("No jobs available. Cannot generate report.");
            return;
        }

        JobPosting selectedJob = jobComboBox.getValue();

        if (selectedJob == null) {
            showError("Please select a job.");
            return;
        }

        if (selectedJob.getId() <= 0) {
            showError("Invalid job selected.");
            return;
        }

        int recruiterId = SessionManager.loggedInUser.getId();

        try {
            report = reportService.generateReport(selectedJob.getId(), recruiterId);
        } catch (Exception e) {
            showError("Failed to generate report: " + e.getMessage());
            return;
        }

        if (report == null) {
            showError("No report data found for this job.");
            return;
        }

        // Populate UI safely
        jobTitleLabel.setText(report.getJobTitle() != null ? report.getJobTitle() : "N/A");
        departmentLabel.setText(report.getDepartment() != null ? report.getDepartment() : "N/A");
        postedDateLabel.setText(report.getPostedOn() != null ? report.getPostedOn() : "N/A");
        totalAppsLabel.setText(String.valueOf(report.getTotalApplications()));
        shortlistedLabel.setText(String.valueOf(report.getTotalShortlisted()));
        generatedDateLabel.setText(
                report.getGeneratedAt() != null ? report.getGeneratedAt().toString() : "N/A"
        );
    }

    @FXML
    public void exportPDF() {

        if (report == null) {
            showError("Generate the report before exporting.");
            return;
        }

        try {
            String path = "RecruitmentReport_" + report.getJobId() + ".pdf";
            reportService.exportReportAsPDF(report, path);
            showSuccess("Report exported successfully:\n" + path);

        } catch (Exception e) {
            showError("Failed to export report: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.show();
    }

    private void showSuccess(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.show();
    }
}
