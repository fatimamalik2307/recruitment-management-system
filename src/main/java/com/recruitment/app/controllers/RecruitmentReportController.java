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

    // Services will be injected by ControllerFactory
    private RecruitmentReportService reportService;
    private JobService jobService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public RecruitmentReportController() {
        // Empty - services will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setServices(RecruitmentReportService reportService, JobService jobService) {
        this.reportService = reportService;
        this.jobService = jobService;
    }

    @FXML
    public void initialize() {
        // Load jobs for recruiter
        int recruiterId = SessionManager.loggedInUser.getId();

        // ADD null check for services
        if (jobService != null) {
            List<JobPosting> jobs = jobService.getAllJobsForRecruiter(recruiterId);
            jobComboBox.setItems(FXCollections.observableArrayList(jobs));
        } else {
            System.err.println("JobService not injected!");
        }
    }

    @FXML
    public void onGenerateReport() {
        // ADD null check for service
        if (reportService == null) {
            showError("Service not initialized!");
            return;
        }

        JobPosting selectedJob = jobComboBox.getValue();
        if (selectedJob == null) {
            showError("Please select a job.");
            return;
        }

        int recruiterId = SessionManager.loggedInUser.getId();
        this.report = reportService.generateReport(selectedJob.getId(), recruiterId);

        if (report == null) {
            showError("No report data found for this job.");
            return;
        }

        // Populate UI
        jobTitleLabel.setText(report.getJobTitle());
        departmentLabel.setText(report.getDepartment());
        postedDateLabel.setText(report.getPostedOn());
        totalAppsLabel.setText(String.valueOf(report.getTotalApplications()));
        shortlistedLabel.setText(String.valueOf(report.getTotalShortlisted()));
        generatedDateLabel.setText(report.getGeneratedAt().toString());
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
            e.printStackTrace();
            showError("Failed to export report.");
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