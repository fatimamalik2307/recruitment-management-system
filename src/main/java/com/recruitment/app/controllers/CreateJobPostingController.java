package com.recruitment.app.controllers;

import com.recruitment.app.models.Company;
import com.recruitment.app.models.JobDescription;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.User;
import com.recruitment.app.services.JobService;
import com.recruitment.app.services.PersonSpecificationService;
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

    // Services will be injected by ControllerFactory
    private JobService jobService;
    private PersonSpecificationService personSpecificationService;

    public static JobDescription selectedDescription = null;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public CreateJobPostingController() {
        // Empty - services will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setJobService(JobService jobService) {
        this.jobService = jobService;
    }

    public void setPersonSpecificationService(PersonSpecificationService service) {
        this.personSpecificationService = service;
    }

    @FXML
    public void initialize() {
        // REMOVED: Manual service instantiation
        // if (jobService == null) { jobService = new JobServiceImpl(...); }

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
        // ADD null check for service
        if (jobService == null) {
            showAlert("Service not initialized!");
            return;
        }

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
        job.setRecruiterId(SessionManager.loggedInUser.getId());
        int recruiterId = job.getRecruiterId();
        // Get hiring manager through service
        User hiringManager = jobService.getHiringManagerForRecruiter(recruiterId);
        if (hiringManager == null) {
            showAlert("No hiring manager found for your company.");
            return;
        }
        job.setHiringManagerId(hiringManager.getId());
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
        // Use DI method
        SceneLoader.loadWithDI(stage, "/ui/job_description_list.fxml", "Saved Descriptions");
    }

    @FXML
    private void openPersonSpecification() {
        Stage stage = (Stage) publishButton.getScene().getWindow();

        // Use DI method - services already injected
        SceneLoader.loadWithDIAndConfig(stage, "/ui/person_specification.fxml", controller -> {
            if (controller instanceof PersonSpecificationController) {
                ((PersonSpecificationController) controller).setPersonSpecificationService(personSpecificationService);
            }
        });
    }

    @FXML
    private void openJobDescription() {
        Stage stage = (Stage) publishButton.getScene().getWindow();
        // Use DI method
        SceneLoader.loadWithDI(stage, "/ui/job_description.fxml", "Job Description");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) publishButton.getScene().getWindow();
        stage.close();
    }
}