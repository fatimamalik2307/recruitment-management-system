package com.recruitment.app.controllers;

import com.recruitment.app.models.JobDescription;
import com.recruitment.app.services.JobDescriptionService;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class JobDescriptionController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> jobTypeCombo;
    @FXML private ComboBox<String> departmentCombo;
    @FXML private TextArea dutiesArea;
    @FXML private TextArea respArea;
    @FXML private TextArea purposeArea;
    @FXML private TextArea reportArea;
    @FXML private TextArea qualificationArea;

    // Service will be injected by ControllerFactory
    private JobDescriptionService service;
    public static JobDescription selectedDescription = null;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public JobDescriptionController() {
        // Empty - service will be injected
    }

    public void setJobDescriptionService(JobDescriptionService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        // REMOVED: Manual service instantiation
        // service = new JobDescriptionServiceImpl(...)

        // Populate dropdowns
        jobTypeCombo.getItems().addAll("Full-Time", "Part-Time", "Contract", "Internship");
        departmentCombo.getItems().addAll(
                "Human Resources", "Marketing", "Finance", "IT",
                "Administration", "Sales", "Operations"
        );

        // PREFILL if editing
        if (selectedDescription != null) {
            titleField.setText(selectedDescription.getTitle());
            dutiesArea.setText(selectedDescription.getDuties());
            respArea.setText(selectedDescription.getResponsibilities());
            purposeArea.setText(selectedDescription.getJobPurpose());
            reportArea.setText(selectedDescription.getReportingStructure());
            qualificationArea.setText(selectedDescription.getRequiredQualification());
            jobTypeCombo.setValue(selectedDescription.getJobType());
            departmentCombo.setValue(selectedDescription.getDepartment());

            selectedDescription = null;
        }
    }

    @FXML
    private void saveDescription() {
        // ADD null check for service
        if (service == null) {
            new Alert(Alert.AlertType.ERROR, "Service not initialized!").show();
            return;
        }

        if (titleField.getText().isEmpty() ||
                jobTypeCombo.getValue() == null ||
                departmentCombo.getValue() == null ||
                dutiesArea.getText().isEmpty() ||
                respArea.getText().isEmpty() ||
                purposeArea.getText().isEmpty() ||
                reportArea.getText().isEmpty() ||
                qualificationArea.getText().isEmpty())
        {
            new Alert(Alert.AlertType.WARNING,
                    "Please fill ALL fields before saving.").show();
            return;
        }

        JobDescription desc = new JobDescription();
        desc.setRecruiterId(SessionManager.loggedInUser.getId());
        desc.setTitle(titleField.getText());
        desc.setDuties(dutiesArea.getText());
        desc.setResponsibilities(respArea.getText());
        desc.setJobPurpose(purposeArea.getText());
        desc.setReportingStructure(reportArea.getText());
        desc.setRequiredQualification(qualificationArea.getText());
        desc.setJobType(jobTypeCombo.getValue());
        desc.setDepartment(departmentCombo.getValue());

        boolean saved = service.saveDescription(desc);

        if (saved) {
            new Alert(Alert.AlertType.INFORMATION,
                    "Job Description Saved Successfully!").showAndWait();

            // clear form
            titleField.clear();
            dutiesArea.clear();
            respArea.clear();
            purposeArea.clear();
            reportArea.clear();
            qualificationArea.clear();
            jobTypeCombo.setValue(null);
            departmentCombo.setValue(null);

        } else {
            new Alert(Alert.AlertType.ERROR,
                    "Error saving job description!").show();
        }
    }

    @FXML
    private void openSavedDescriptions() {
        JobDescriptionListController.caller = "edit_description";
        Stage stage = (Stage) titleField.getScene().getWindow();
        SceneLoader.load(stage, "/ui/job_description_list.fxml");
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        SceneLoader.load(stage, "/ui/create_job_posting.fxml");
    }

    public static void prefill(JobDescription desc) {
        selectedDescription = desc;
    }
}