package com.recruitment.app.controllers;

import com.recruitment.app.models.AssessmentResult;
import com.recruitment.app.services.RecruiterService;
import com.recruitment.app.services.AssessmentService;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AssessmentFormController {

    @FXML private TextField technicalScoreField;
    @FXML private TextField hrScoreField;
    @FXML private TextArea remarksArea;
    @FXML private Button submitBtn;
    @FXML private Button cancelBtn;

    // Services injected by ControllerFactory
    private RecruiterService recruiterService;
    private AssessmentService assessmentService;

    // IDs for the current assessment
    private int shortlistId;
    private int applicationId;

    // ---------- DEFAULT CONSTRUCTOR (for ControllerFactory) ----------
    public AssessmentFormController() {
        // Empty - services will be injected via setter
    }

    // ---------- SERVICE INJECTION (Setter instead of Constructor) ----------
    public void setServices(RecruiterService recruiterService, AssessmentService assessmentService) {
        this.recruiterService = recruiterService;
        this.assessmentService = assessmentService;
    }

    // Pass contextual IDs separately
    public void setContext(int shortlistId, int applicationId) {
        this.shortlistId = shortlistId;
        this.applicationId = applicationId;
    }

    @FXML
    private void initialize() {
        submitBtn.setOnAction(e -> saveAssessment());
        cancelBtn.setOnAction(e -> closeWindow());
    }

    private void saveAssessment() {
        try {
            double technical = Double.parseDouble(technicalScoreField.getText());
            double hr = Double.parseDouble(hrScoreField.getText());
            String remarks = remarksArea.getText();

            int recruiterId = SessionManager.loggedInUser.getId();

            AssessmentResult result = new AssessmentResult(shortlistId, recruiterId, technical, hr, remarks);
            assessmentService.recordAssessment(result);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Assessment saved successfully!");
            alert.showAndWait();

            closeWindow();

        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Please enter valid numeric scores!").show();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error saving assessment!").show();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) submitBtn.getScene().getWindow();
        stage.close();
    }
}