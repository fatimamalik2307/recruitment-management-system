package com.recruitment.app.controllers;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.JobService;
import com.recruitment.app.utils.SceneLoader;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;

import java.time.LocalDate;
import java.util.List;

public class BrowseJobsController {

    @FXML private VBox jobsContainer; // container where job cards appear

    private JobService jobService;

    public BrowseJobsController() {}

    // Injected via ControllerFactory
    public void setJobService(JobService jobService) {
        System.out.println("BrowseJobsController: JobService injected = " + (jobService != null));
        this.jobService = jobService;
    }

    @FXML
    public void initialize() {
        System.out.println("BrowseJobsController: initialize() called");
        loadJobs();
    }

    // --------------------------------------------------------
    // 🔵 NEW JOB CARD UI (Dark Theme, CSS-based)
    // --------------------------------------------------------
    private VBox createJobCard(JobPosting job) {

        // Title
        Label titleLabel = new Label(job.getTitle());
        titleLabel.getStyleClass().add("job-title");

        // Department + Deadline
        Label deptLabel = new Label(
                job.getDepartment() +
                        "   |   Deadline: " +
                        (job.getDeadline() != null ? job.getDeadline() : "N/A")
        );
        deptLabel.getStyleClass().add("job-dept");

        VBox infoBox = new VBox(5, titleLabel, deptLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Apply Button
        Button applyBtn = new Button("Apply Now");
        applyBtn.getStyleClass().add("apply-btn");

        applyBtn.setOnAction(ev -> openJobDetails(job, ev));

        // Card row layout
        HBox row = new HBox(infoBox, applyBtn);
        row.setSpacing(20);
        row.setPadding(new Insets(10));

        // Outer card container
        VBox card = new VBox(row);
        card.getStyleClass().add("job-card");
        card.setMaxWidth(900);

        return card;
    }

    // --------------------------------------------------------
    // Deadline extraction helper (for filtering expired posts)
    // --------------------------------------------------------
    private LocalDate extractDeadline(Object deadlineObj) {
        if (deadlineObj == null) return null;
        if (deadlineObj instanceof LocalDate) return (LocalDate) deadlineObj;
        if (deadlineObj instanceof java.sql.Date) return ((java.sql.Date) deadlineObj).toLocalDate();
        if (deadlineObj instanceof java.util.Date)
            return ((java.util.Date) deadlineObj).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

        if (deadlineObj instanceof String) {
            return LocalDate.parse(((String) deadlineObj).replace("\"", ""));
        }
        return null;
    }

    // --------------------------------------------------------
    // Load jobs from DB and display UI cards
    // --------------------------------------------------------
    private void loadJobs() {
        try {
            if (jobService == null) {
                showErrorAlert("System error: JobService not initialized.");
                return;
            }

            List<JobPosting> activeJobs = jobService.getAllJobs();

            jobsContainer.getChildren().clear();

            if (activeJobs.isEmpty()) {
                Label noJobsLabel = new Label("No active job postings found at this time.");
                noJobsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #99aaff; -fx-padding: 50;");
                jobsContainer.getChildren().add(noJobsLabel);
            } else {
                for (JobPosting job : activeJobs) {
                    jobsContainer.getChildren().add(createJobCard(job));
                }
            }

            System.out.println("Loaded " + activeJobs.size() + " active jobs");

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Failed to load jobs: " + e.getMessage());
        }
    }

    // --------------------------------------------------------
    // Open job details view
    // --------------------------------------------------------
    private void openJobDetails(JobPosting job, javafx.event.ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            SceneLoader.loadWithDIAndConfig(stage, "/ui/job_details.fxml", controller -> {
                if (controller instanceof JobDetailsController jdc) {
                    jdc.setJob(job);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Failed to open job details");
        }
    }

    // --------------------------------------------------------
    // Error popup helper
    // --------------------------------------------------------
    private void showErrorAlert(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    // --------------------------------------------------------
    // Navigation buttons
    // --------------------------------------------------------
    @FXML
    public void logout(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.loadWithDI(stage, "/ui/login.fxml", "Login");
    }

    @FXML
    public void openProfile(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.loadWithDI(stage, "/ui/profile.fxml", "Update Profile");
    }

    @FXML
    public void openTrackApplications(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.loadWithDI(stage, "/ui/track_applications.fxml", "Track Applications");
    }

    @FXML
    public void openChangePassword(javafx.event.ActionEvent event) {
        try {
            Stage stage = new Stage();
            SceneLoader.loadWithDI(stage, "/ui/change_password.fxml", "Change Password");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Cannot open change password screen");
        }
    }
}
