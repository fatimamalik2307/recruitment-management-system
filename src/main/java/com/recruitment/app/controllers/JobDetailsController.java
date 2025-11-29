package com.recruitment.app.controllers;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.utils.SceneLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class JobDetailsController {

    private JobPosting job; // non-static field

    @FXML private Label titleLabel;
    @FXML private Label deptLabel;
    @FXML private Label descLabel;
    @FXML private Label reqLabel;
    @FXML private Label deadlineLabel;

    // Setter to pass the job from previous controller
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
            deadlineLabel.setText("Deadline: " + (job.getDeadline() != null ? job.getDeadline().toString() : "N/A"));
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/browse_jobs.fxml");
    }
}
