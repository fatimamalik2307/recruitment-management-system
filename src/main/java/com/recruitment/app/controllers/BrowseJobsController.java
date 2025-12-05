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

    // --- FXML element injection ---
    @FXML
    private VBox jobsContainer; // New container for job cards (Replaces TableView)

    // Service injected by ControllerFactory
    private JobService jobService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public BrowseJobsController() {
        // Empty - services will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setJobService(JobService jobService) {
        System.out.println("BrowseJobsController: JobService injected = " + (jobService != null));
        this.jobService = jobService;
    }

    @FXML
    public void initialize() {
        System.out.println("BrowseJobsController: initialize() called");
        // No more TableView mapping, just load the jobs into the VBox container
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


    private LocalDate extractDeadline(Object deadlineObj) {

        if (deadlineObj == null)
            return null;

        if (deadlineObj instanceof LocalDate)
            return (LocalDate) deadlineObj;

        if (deadlineObj instanceof java.sql.Date)
            return ((java.sql.Date) deadlineObj).toLocalDate();

        if (deadlineObj instanceof java.util.Date)
            return ((java.util.Date) deadlineObj).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

        if (deadlineObj instanceof String) {
            String s = ((String) deadlineObj).replace("\"", ""); // remove quotes
            return LocalDate.parse(s); // must be yyyy-MM-dd
        }

        return null; // unknown type
    }

    private void loadJobs() {
        try {
            if (jobService != null) {
                System.out.println("Loading jobs...");

                List<JobPosting> activeJobs = jobService.getAllJobs().stream()
                        .filter(job -> {
                            LocalDate d = extractDeadline(job.getDeadline());
                            return d == null || !d.isBefore(LocalDate.now());
                        })
                        .toList();

                jobsContainer.getChildren().clear(); // Clear existing content
                for (JobPosting job : jobService.getAllJobs()) {
                    System.out.println("Raw deadline = " + job.getDeadline() + " | Type = " +
                            (job.getDeadline() != null ? job.getDeadline().getClass() : "null"));
                }

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
            } else {
                System.err.println("ERROR: JobService is NULL!");
                showErrorAlert("System error: Services not initialized");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Failed to load jobs: " + e.getMessage());
        }
    }


    private void openJobDetails(JobPosting job, javafx.event.ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            SceneLoader.loadWithDIAndConfig(stage, "/ui/job_details.fxml", controller -> {
                if (controller instanceof JobDetailsController jdc) {
                    jdc.setJob(job);     // FIX ✔
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Failed to open job details");
        }
    }

    private void showErrorAlert(String message) {
        // NOTE: Standard JavaFX alerts are used here,
        // replace with custom modal dialogs if needed to avoid blocking in specific environments.
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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