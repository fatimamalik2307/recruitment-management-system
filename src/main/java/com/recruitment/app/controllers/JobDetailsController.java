package com.recruitment.app.controllers;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.ApplicationService;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;

public class JobDetailsController {

    private JobPosting job;

    // Service will be injected by ControllerFactory
    private ApplicationService applicationService;

    @FXML private Label titleLabel;
    @FXML private Label deptLabel;
    @FXML private Label descLabel;
    @FXML private Label reqLabel;
    @FXML private Label deadlineLabel;


    public JobDetailsController() {
        // Empty - service will be injected
    }
    @FXML
    public void initialize() {
        if (SessionManager.selectedJob != null) {
            setJob(SessionManager.selectedJob);
        }
    }



    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public void setJob(JobPosting job) {
        this.job = job;
        populateFields();
    }

    private void populateFields() {
        if (job != null) {
            titleLabel.setText("Title: " + job.getTitle());
            deptLabel.setText("Department: " + job.getDepartment());
            descLabel.setText("Description: " + job.getDescription());
            reqLabel.setText("Required: " + job.getRequiredQualification());
            deadlineLabel.setText("Deadline: " +
                    (job.getDeadline() != null ? job.getDeadline().toString() : "N/A"));
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/browse_jobs.fxml");
    }

    @FXML
    public void openApplicationForm(ActionEvent event) {
        SessionManager.selectedJob = job;  // ← SAVE JOB before leaving page

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.loadApplicationForm(stage, "/ui/application_form.fxml", applicationService, job);
    }

}