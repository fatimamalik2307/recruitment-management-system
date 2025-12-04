package com.recruitment.app.controllers;

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

    private JobService jobService;
    private PersonSpecificationService personSpecificationService;

    public static JobDescription selectedDescription = null;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public CreateJobPostingController() {}

    public static void prefillFromDescription(JobDescription desc) {
        selectedDescription = desc;
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

        jobTypeCombo.getItems().addAll("Full-Time", "Part-Time", "Contract", "Internship");

        deptCombo.getItems().addAll(
                "Human Resources", "Marketing", "Finance", "IT",
                "Administration", "Sales", "Operations"
        );

        // Autofill from saved description
        if (selectedDescription != null) {
            titleField.setText(selectedDescription.getTitle());
            descriptionArea.setText(selectedDescription.getDuties());
            locationField.setText("");
            jobTypeCombo.setValue(selectedDescription.getJobType());
            deptCombo.setValue(selectedDescription.getDepartment());
            qualificationField.setText(selectedDescription.getRequiredQualification());

            selectedDescription = null;
        }
    }

    @FXML
    private void publishJob() {

        if (jobService == null) {
            showAlert("Service not initialized!");
            return;
        }

        // Required field validations
        if (titleField.getText().trim().isEmpty() ||
                deptCombo.getValue() == null ||
                descriptionArea.getText().trim().isEmpty() ||
                qualificationField.getText().trim().isEmpty() ||
                jobTypeCombo.getValue() == null ||
                deadlinePicker.getValue() == null) {

            showAlert("Please fill all required fields.");
            return;
        }

        // Location must not be empty
        if (locationField.getText().trim().isEmpty()) {
            showAlert("Location cannot be empty.");
            return;
        }

        // Deadline must be future
        if (deadlinePicker.getValue().isBefore(java.time.LocalDate.now())) {
            showAlert("Deadline must be a future date.");
            return;
        }

        // Salary validation
        String salary = salaryField.getText().trim();
        if (!salary.isEmpty() && !salary.matches("\\d+(\\.\\d+)?")) {
            showAlert("Salary must be a valid number.");
            return;
        }

        JobPosting job = new JobPosting(
                titleField.getText().trim(),
                deptCombo.getValue(),
                descriptionArea.getText().trim(),
                qualificationField.getText().trim(),
                locationField.getText().trim(),
                deadlinePicker.getValue(),
                jobTypeCombo.getValue(),
                salary,
                SessionManager.loggedInUser.getId()
        );

        job.setRecruiterId(SessionManager.loggedInUser.getId());

        // Get hiring manager
        User hiringManager = jobService.getHiringManagerForRecruiter(job.getRecruiterId());

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

    // ---------- Helper Methods ----------

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) publishButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void openSavedDescriptions() {
        Stage stage = (Stage) publishButton.getScene().getWindow();
        SceneLoader.loadWithDI(stage, "/ui/job_description_list.fxml", "Saved Descriptions");
    }

    @FXML
    private void openPersonSpecification() {
        Stage stage = (Stage) publishButton.getScene().getWindow();
        SceneLoader.loadWithDIAndConfig(stage, "/ui/person_specification.fxml", controller -> {
            if (controller instanceof PersonSpecificationController) {
                ((PersonSpecificationController) controller).setPersonSpecificationService(personSpecificationService);
            }
        });
    }

    @FXML
    private void openJobDescription() {
        Stage stage = (Stage) publishButton.getScene().getWindow();
        SceneLoader.loadWithDI(stage, "/ui/job_description.fxml", "Job Description");
    }
}
