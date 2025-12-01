package com.recruitment.app.controllers;

import com.recruitment.app.dao.ApplicationDAOImpl;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.ApplicationServiceImpl;
import com.recruitment.app.utils.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;

public class JobDetailsController {

    private JobPosting job;

    @FXML private Label titleLabel;
    @FXML private Label deptLabel;
    @FXML private Label descLabel;
    @FXML private Label reqLabel;
    @FXML private Label deadlineLabel;

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
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        SceneLoader.loadWithData(stage, "/ui/application_form.fxml", controller -> {

            ApplicationFormController form = (ApplicationFormController) controller;

            // STEP 1 — Pass the Job
            form.setJob(job);

            // STEP 2 — Inject ApplicationService
            form.setApplicationService(
                    new ApplicationServiceImpl(
                            new ApplicationDAOImpl(com.recruitment.app.config.DBConnection.getConnection())
                    )
            );
        });
    }

}
