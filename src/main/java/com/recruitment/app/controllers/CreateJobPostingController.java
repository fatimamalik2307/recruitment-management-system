package com.recruitment.app.controllers;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.JobService;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class CreateJobPostingController {

    @FXML private TextField titleField;
    @FXML private TextField deptField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField qualificationField;
    @FXML private TextField locationField;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextField jobTypeField;
    @FXML private TextField salaryField;
    @FXML private Button publishButton;

    private JobService jobService; // injected

    public void setJobService(JobService jobService) {
        this.jobService = jobService;
    }

    @FXML
    private void publishJob() {
        if (titleField.getText().isEmpty() || deptField.getText().isEmpty() ||
                qualificationField.getText().isEmpty() || deadlinePicker.getValue() == null) {
            showAlert("Please complete all required fields.");
            return;
        }

        JobPosting job = new JobPosting(
                titleField.getText(),
                deptField.getText(),
                descriptionArea.getText(),
                qualificationField.getText(),
                locationField.getText(),
                deadlinePicker.getValue(),
                jobTypeField.getText(),
                salaryField.getText(),
                SessionManager.loggedInUser.getId()
        );

        boolean success = jobService.createJob(job);
        if (success) {
            showAlert("Job posted successfully!");
            closeWindow();
        } else {
            showAlert("Failed to post job. Try again.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) publishButton.getScene().getWindow();
        stage.close();
    }
}
