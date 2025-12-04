package com.recruitment.app.controllers;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.ShortlistingCriteria;
import com.recruitment.app.services.RecruiterService;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.List;

public class ShortlistingCriteriaController {

    @FXML private ComboBox<JobPosting> jobComboBox;
    @FXML private TextField minExperienceField;
    @FXML private TextField qualificationField;
    @FXML private TextField skillsField;
    @FXML private TextField locationField;
    @FXML private TextField gradeField;

    // Service will be injected by ControllerFactory
    private RecruiterService recruiterService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public ShortlistingCriteriaController() {
        // Empty - service will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setRecruiterService(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @FXML
    public void initialize() {
        // ADD null check for service
        if (recruiterService == null) {
            System.err.println("RecruiterService not injected!");
            return;
        }

        // Populate jobs for the logged-in recruiter
        List<JobPosting> jobs = recruiterService.getJobsByRecruiter(SessionManager.loggedInUser.getId());
        if (jobs != null && !jobs.isEmpty()) {
            jobComboBox.getItems().addAll(jobs);
        }

        // Load existing criteria when job is selected
        jobComboBox.setOnAction(e -> loadCriteria());
    }

    private void loadCriteria() {
        // ADD null check for service
        if (recruiterService == null) return;

        JobPosting selectedJob = jobComboBox.getSelectionModel().getSelectedItem();
        if (selectedJob == null) return;

        ShortlistingCriteria criteria = recruiterService.getShortlistingCriteria(selectedJob.getId());
        if (criteria != null) {
            minExperienceField.setText(criteria.getMinExperience() != null ? criteria.getMinExperience().toString() : "");
            qualificationField.setText(criteria.getRequiredQualification());
            skillsField.setText(criteria.getRequiredSkills());
            locationField.setText(criteria.getOptionalLocation());
            gradeField.setText(criteria.getOptionalGrade());
        } else {
            // Clear fields if no criteria yet
            minExperienceField.clear();
            qualificationField.clear();
            skillsField.clear();
            locationField.clear();
            gradeField.clear();
        }
    }

    @FXML
    private void saveCriteria() {
        if (recruiterService == null) {
            new Alert(Alert.AlertType.ERROR, "Service not initialized!").show();
            return;
        }

        JobPosting job = jobComboBox.getSelectionModel().getSelectedItem();
        if (job == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a job.").show();
            return;
        }

        // Validate numeric experience
        if (!minExperienceField.getText().trim().isEmpty() &&
                !minExperienceField.getText().matches("\\d+")) {
            new Alert(Alert.AlertType.ERROR, "Experience must be a whole number.").show();
            return;
        }

        if (minExperienceField.getText().matches("-\\d+")) {
            new Alert(Alert.AlertType.ERROR, "Experience cannot be negative.").show();
            return;
        }

        // Must have at least one real criterion
        if (qualificationField.getText().trim().isEmpty() &&
                skillsField.getText().trim().isEmpty() &&
                minExperienceField.getText().trim().isEmpty()) {

            new Alert(Alert.AlertType.WARNING, "Enter at least one criterion.").show();
            return;
        }

        
        if (!gradeField.getText().trim().isEmpty() &&
                !gradeField.getText().matches("[A-Ca-c]|[1-4]")) {

            new Alert(Alert.AlertType.WARNING, "Grade must be A, B, C or 1–4.").show();
            return;
        }

        ShortlistingCriteria criteria = new ShortlistingCriteria();
        criteria.setJobId(job.getId());
        criteria.setMinExperience(minExperienceField.getText().isEmpty() ? null :
                Integer.parseInt(minExperienceField.getText()));
        criteria.setRequiredQualification(qualificationField.getText());
        criteria.setRequiredSkills(skillsField.getText());
        criteria.setOptionalLocation(locationField.getText());
        criteria.setOptionalGrade(gradeField.getText());

        recruiterService.saveShortlistingCriteria(criteria);

        new Alert(Alert.AlertType.INFORMATION, "Shortlisting criteria saved successfully.").showAndWait();
    }

}