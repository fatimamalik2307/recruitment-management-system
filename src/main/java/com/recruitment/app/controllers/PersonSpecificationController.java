package com.recruitment.app.controllers;

import com.recruitment.app.models.PersonSpecification;
import com.recruitment.app.services.PersonSpecificationService;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PersonSpecificationController {

    @FXML private TextArea skillsArea;
    @FXML private TextArea experienceArea;
    @FXML private TextArea educationArea;
    @FXML private TextArea traitsArea;

    // Service will be injected by ControllerFactory
    private PersonSpecificationService specService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public PersonSpecificationController() {
        // Empty - service will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setPersonSpecificationService(PersonSpecificationService service) {
        this.specService = service;
    }


    @FXML
    private void saveSpecification() {
        if (specService == null) {
            new Alert(Alert.AlertType.ERROR, "Service not injected!").show();
            return;
        }

        String skills = skillsArea.getText().trim();
        String experience = experienceArea.getText().trim();
        String education = educationArea.getText().trim();
        String traits = traitsArea.getText().trim();

        // Required checks
        if (skills.isEmpty() || experience.isEmpty() || education.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Skills, Experience, and Education fields are required!").show();
            return;
        }

        // Length validations
        if (skills.length() < 10) {
            new Alert(Alert.AlertType.WARNING, "Skills description must be at least 10 characters.").show();
            return;
        }
        if (experience.length() < 10) {
            new Alert(Alert.AlertType.WARNING, "Experience description must be at least 10 characters.").show();
            return;
        }
        if (education.length() < 5) {
            new Alert(Alert.AlertType.WARNING, "Education details must be at least 5 characters.").show();
            return;
        }

        if (!traits.isEmpty() && traits.length() < 5) {
            new Alert(Alert.AlertType.WARNING, "Traits must be at least 5 characters if entered.").show();
            return;
        }

        PersonSpecification spec = new PersonSpecification(
                SessionManager.loggedInUser.getId(),
                skills,
                experience,
                education,
                traits
        );

        boolean saved = specService.save(spec);

        if (saved) {
            new Alert(Alert.AlertType.INFORMATION, "Person Specification Saved!", ButtonType.OK).showAndWait();
            Stage stage = (Stage) skillsArea.getScene().getWindow();
            SceneLoader.loadWithDI(stage, "/ui/create_job_posting.fxml", "Create Job Posting");
        } else {
            new Alert(Alert.AlertType.ERROR, "Error saving person specification").show();
        }
    }


    @FXML
    private void cancel() {
        Stage stage = (Stage) skillsArea.getScene().getWindow();
        // Use DI method
        SceneLoader.loadWithDI(stage, "/ui/create_job_posting.fxml", "Create Job Posting");
    }
}