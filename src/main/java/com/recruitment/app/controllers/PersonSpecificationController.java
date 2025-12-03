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
        // KEEP null check (good practice)
        if (specService == null) {
            new Alert(Alert.AlertType.ERROR, "Service not injected!").show();
            return;
        }

        PersonSpecification spec = new PersonSpecification(
                SessionManager.loggedInUser.getId(),
                skillsArea.getText(),
                experienceArea.getText(),
                educationArea.getText(),
                traitsArea.getText()
        );

        boolean saved = specService.save(spec);

        if (saved) {
            new Alert(Alert.AlertType.INFORMATION, "Person Specification Saved!", ButtonType.OK).showAndWait();
            Stage stage = (Stage) skillsArea.getScene().getWindow();
            // Use DI method for navigation back
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