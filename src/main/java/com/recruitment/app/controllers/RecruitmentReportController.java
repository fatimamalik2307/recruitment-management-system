package com.recruitment.app.controllers;

import com.recruitment.app.dao.JobDAO;
import com.recruitment.app.dao.JobDAOImpl;
import com.recruitment.app.models.RecruitmentReport;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.RecruitmentReportService;
import com.recruitment.app.services.RecruitmentReportServiceImpl;
import com.recruitment.app.services.JobService;
import com.recruitment.app.services.JobServiceImpl;
import com.recruitment.app.utils.DBConnection;
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

    // Services
    private final RecruitmentReportService reportService = new RecruitmentReportServiceImpl();
    private final JobDAO jobDAO = new JobDAOImpl(DBConnection.getConnection());
    private final JobService jobService = new JobServiceImpl(jobDAO);

    @FXML
    public void initialize() {
        // Load jobs for recruiter (hardcoded recruiterId for example, normally get from session)
        int recruiterId = SessionManager.loggedInUser.getId();
        List<JobPosting> jobs = jobService.getAllJobsForRecruiter(recruiterId);
        jobComboBox.setItems(FXCollections.observableArrayList(jobs));
    }

    @FXML
    public void onGenerateReport() {
        JobPosting selectedJob = jobComboBox.getValue();
        if (selectedJob == null) {
            showError("Please select a job.");
            return;
        }

        int recruiterId = SessionManager.loggedInUser.getId(); // normally from logged-in session
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
