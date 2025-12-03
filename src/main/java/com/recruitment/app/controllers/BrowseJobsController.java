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
    @FXML private VBox jobsContainer; // New container for job cards (Replaces TableView)

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

    /**
     * Creates and returns a beautifully styled job card (VBox) for a single job posting.
     */
    private VBox createJobCard(JobPosting job) {
        // 1. Job Details Section (Title, Dept, Deadline)
        Label titleLabel = new Label(job.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1a2c5a;");

        Label detailLabel = new Label(job.getDepartment() + " | Deadline: " +
                (job.getDeadline() != null ? job.getDeadline().toString() : "N/A"));
        detailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #44598a;");

        VBox infoBox = new VBox(5, titleLabel, detailLabel);
        VBox.setVgrow(infoBox, Priority.ALWAYS);

        // 2. Action Button (Apply/View)
        Button applyBtn = new Button("Apply Now");
        // Inline style for the button to ensure it looks good, matching our blue theme
        applyBtn.setStyle("-fx-background-color: #4f7cfe; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.2, 0, 2);");

        applyBtn.setOnAction(event -> openJobDetails(job, event));

        // 3. Main Card Layout (HBox)
        HBox cardContent = new HBox(20, infoBox, applyBtn);
        cardContent.setPadding(new Insets(15));
        cardContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // 4. Outer VBox (The Card itself)
        VBox card = new VBox(cardContent);
        card.setMaxWidth(700);
        // Add CSS class defined in style.css for the card's 3D look
        card.getStyleClass().add("job-card");

        return card;
    }

    private void loadJobs() {
        try {
            if (jobService != null) {
                System.out.println("Loading jobs...");

                List<JobPosting> activeJobs = jobService.getAllJobs().stream()
                        .filter(job -> job.getDeadline() == null ||
                                !job.getDeadline().isBefore(LocalDate.now()))
                        .toList();

                jobsContainer.getChildren().clear(); // Clear existing content

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
                    jdc.setJob(job);     // FIX ✔️
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