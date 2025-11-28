package com.recruitment.app.controllers;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.utils.SceneLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class JobDetailsController {

    public static JobPosting selectedJob;

    @FXML private Label titleLabel;
    @FXML private Label deptLabel;
    @FXML private Label descLabel;
    @FXML private Label reqLabel;
    @FXML private Label deadlineLabel;

    @FXML
    public void initialize() {
        if (selectedJob != null) {
            titleLabel.setText("Title: " + selectedJob.getJobTitle());
            deptLabel.setText("Department: " + selectedJob.getDepartment());
            descLabel.setText("Description: " + selectedJob.getDescription());
            reqLabel.setText("Required: " + selectedJob.getRequiredQualification());
            deadlineLabel.setText("Deadline: " + selectedJob.getDeadline());
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/browse_jobs.fxml");
    }
}
