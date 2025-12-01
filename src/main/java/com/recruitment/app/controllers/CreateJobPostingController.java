package com.recruitment.app.controllers;

import com.recruitment.app.dao.JobDAOImpl;
import com.recruitment.app.dao.PersonSpecificationDAOImpl;
import com.recruitment.app.models.JobDescription;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.JobService;
import com.recruitment.app.services.JobServiceImpl;
import com.recruitment.app.services.PersonSpecificationServiceImpl;
import com.recruitment.app.utils.DBConnection;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreateJobPostingController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> deptCombo;
    @FXML private TextArea descriptionArea;
    @FXML private TextField qualificationField;
    @FXML private TextField locationField;
    @FXML private DatePicker deadlinePicker;
    @FXML private ComboBox<String> jobTypeCombo;
    @FXML private TextField salaryField;

    @FXML private Button publishButton;

    private JobService jobService;
    public static JobDescription selectedDescription = null;

    public void setJobService(JobService jobService) {
        this.jobService = jobService;
    }
    @FXML
    public void initialize() {

        if (jobService == null) {
            jobService = new JobServiceImpl(new JobDAOImpl(DBConnection.getConnection()));
        }

        // Populate dropdowns
        jobTypeCombo.getItems().addAll("Full-Time", "Part-Time", "Contract", "Internship");
        deptCombo.getItems().addAll(
                "Human Resources", "Marketing", "Finance", "IT",
                "Administration", "Sales", "Operations"
        );

        // Auto-fill from Job Description
        if (selectedDescription != null) {

            titleField.setText(selectedDescription.getTitle());
            descriptionArea.setText(selectedDescription.getDuties());
            locationField.setText(""); // user fills manually

            jobTypeCombo.setValue(selectedDescription.getJobType());
            deptCombo.setValue(selectedDescription.getDepartment());
            qualificationField.setText(selectedDescription.getRequiredQualification());

            selectedDescription = null; // reset for next time
        }
    }

    @FXML
    private void publishJob() {

        if (titleField.getText().isEmpty()
                || deptCombo.getValue() == null
                || descriptionArea.getText().isEmpty()
                || qualificationField.getText().isEmpty()
                || jobTypeCombo.getValue() == null
                || deadlinePicker.getValue() == null) {

            showAlert("Please fill all required fields.");
            return;
        }

        JobPosting job = new JobPosting(
                titleField.getText(),
                deptCombo.getValue(),
                descriptionArea.getText(),
                qualificationField.getText(),
                locationField.getText(),
                deadlinePicker.getValue(),
                jobTypeCombo.getValue(),
                salaryField.getText(),
                SessionManager.loggedInUser.getId()
        );

        boolean success = jobService.createJob(job);

        if (success) {
            showAlert("Job Posted Successfully!");
            closeWindow();
        } else {
            showAlert("Failed to post job.");
        }
    }

    public static void prefillFromDescription(JobDescription desc) {
        selectedDescription = desc;
    }

    @FXML
    private void openSavedDescriptions() {
        Stage stage = (Stage) publishButton.getScene().getWindow();
        SceneLoader.load(stage, "/ui/job_description_list.fxml");
    }
    @FXML
    private void openPersonSpecification() {
        Stage stage = (Stage) publishButton.getScene().getWindow();

        SceneLoader.loadWithData(stage, "/ui/person_specification.fxml", controller -> {
            ((PersonSpecificationController) controller).setPersonSpecificationService(
                    new PersonSpecificationServiceImpl(
                            new PersonSpecificationDAOImpl(
                                    com.recruitment.app.config.DBConnection.getConnection()
                            )
                    )
            );
        });
    }

    @FXML
    private void openJobDescription() {
        Stage stage = (Stage) publishButton.getScene().getWindow();
        SceneLoader.load(stage, "/ui/job_description.fxml");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) publishButton.getScene().getWindow();
        stage.close();
    }
}
