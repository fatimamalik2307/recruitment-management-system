package com.recruitment.app.controllers;

import com.recruitment.app.models.Application;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.RecruiterService;
import com.recruitment.app.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ReviewApplicationsController {

    @FXML private ComboBox<JobPosting> jobComboBox;
    @FXML private TableView<Application> applicationsTable;
    @FXML private TableColumn<Application, String> applicantNameCol;
    @FXML private TableColumn<Application, String> appliedDateCol;
    @FXML private TableColumn<Application, String> statusCol;
    @FXML private TableColumn<Application, Void> actionCol;

    // Service will be injected by ControllerFactory
    private RecruiterService recruiterService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public ReviewApplicationsController() {
        // Empty - service will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setRecruiterService(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    private void showApplicationDetails(Application app) {
        StringBuilder details = new StringBuilder();
        details.append("Qualification: ").append(app.getQualification()).append("\n");
        details.append("Experience: ").append(app.getExperience()).append("\n");
        details.append("Cover Letter: ").append(app.getCoverLetter()).append("\n");
        details.append("Status: ").append(app.getStatus()).append("\n");
        details.append("Applied At: ").append(app.getAppliedAt()).append("\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Application Details");
        alert.setHeaderText("Details for applicant: " + getApplicantName(app.getUserId()));
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        // ADD null check for service
        if (recruiterService == null) {
            System.err.println("RecruiterService not injected!");
            return;
        }

        // Populate jobs for the logged-in recruiter
        List<JobPosting> jobs = recruiterService.getJobsByRecruiter(SessionManager.loggedInUser.getId());
        if (jobs != null && !jobs.isEmpty()) {
            jobComboBox.getItems().addAll(jobs);
        }

        // Table column setup
        applicantNameCol.setCellValueFactory(cell ->
                new SimpleStringProperty(getApplicantName(cell.getValue().getUserId()))
        );
        appliedDateCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAppliedAt().toString())
        );
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load applications when a job is selected
        jobComboBox.setOnAction(e -> loadApplications());

        // Add a "View Details" button in each row
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("View Details");

            {
                viewButton.setOnAction(e -> {
                    Application app = getTableView().getItems().get(getIndex());
                    showApplicationDetails(app);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
    }

    @FXML
    private void loadApplications() {
        // ADD null check for service
        if (recruiterService == null) {
            new Alert(Alert.AlertType.ERROR, "Service not initialized!").show();
            return;
        }

        JobPosting selectedJob = jobComboBox.getSelectionModel().getSelectedItem();
        if (selectedJob == null) return;

        List<Application> apps = recruiterService.getApplicationsForJob(selectedJob.getId());
        if (apps.isEmpty()) {
            applicationsTable.getItems().clear();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No applications received for this job.");
            alert.showAndWait();
        } else {
            applicationsTable.getItems().setAll(apps);
        }
    }

    // Fetch applicant name using service or DAO
    private String getApplicantName(int userId) {
        // Use the injected service instead of creating new DAO
        if (recruiterService != null) {
            return recruiterService.getApplicantName(userId);
        }
        return "Unknown Applicant";
    }
}