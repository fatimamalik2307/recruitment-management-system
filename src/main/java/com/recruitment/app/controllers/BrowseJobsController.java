package com.recruitment.app.controllers;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.JobService;
import com.recruitment.app.utils.SceneLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.time.LocalDate;

public class BrowseJobsController {

    @FXML private TableView<JobPosting> jobsTable;
    @FXML private TableColumn<JobPosting, String> titleCol;
    @FXML private TableColumn<JobPosting, String> deptCol;
    @FXML private TableColumn<JobPosting, String> deadlineCol;
    @FXML private TableColumn<JobPosting, Void> actionCol;

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

        // Map table columns
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        deptCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment()));
        deadlineCol.setCellValueFactory(data -> {
            if (data.getValue().getDeadline() != null) {
                return new SimpleStringProperty(data.getValue().getDeadline().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        // Action button
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View");

            {
                btn.setOnAction(event -> {
                    JobPosting job = getTableView().getItems().get(getIndex());
                    openJobDetails(job, event);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Load jobs from service
        loadJobs();
    }

    private void loadJobs() {
        try {
            if (jobService != null) {
                System.out.println("Loading jobs...");
                jobsTable.getItems().setAll(
                        jobService.getAllJobs().stream()
                                .filter(job -> job.getDeadline() == null ||
                                        !job.getDeadline().isBefore(LocalDate.now()))
                                .toList()
                );
                System.out.println("Loaded " + jobsTable.getItems().size() + " jobs");
            } else {
                System.err.println("ERROR: JobService is NULL!");
                new Alert(Alert.AlertType.ERROR, "System error: Services not initialized").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load jobs: " + e.getMessage()).show();
        }
    }

    private void openJobDetails(JobPosting job, javafx.event.ActionEvent event) {
        try {
            // USE SceneLoader with DI
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneLoader.loadWithDIAndConfig(stage, "/ui/job_details.fxml", controller -> {
                if (controller instanceof JobDetailsController) {
                    ((JobDetailsController) controller).setJob(job);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open job details").show();
        }
    }

    @FXML
    public void logout(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // USE DI METHOD
        SceneLoader.loadWithDI(stage, "/ui/login.fxml", "Login");
    }

    @FXML
    public void openProfile(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // USE DI METHOD
        SceneLoader.loadWithDI(stage, "/ui/profile.fxml", "Update Profile");
    }

    @FXML
    public void openTrackApplications(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // USE DI METHOD
        SceneLoader.loadWithDI(stage, "/ui/track_applications.fxml", "Track Applications");
    }

    @FXML
    public void openChangePassword(javafx.event.ActionEvent event) {
        try {
            Stage stage = new Stage();
            // USE DI METHOD
            SceneLoader.loadWithDI(stage, "/ui/change_password.fxml", "Change Password");
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Cannot open change password screen").show();
        }
    }
}