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

    private RecruiterService recruiterService;
    private AssessmentService assessmentService;

    private int shortlistId;
    private int applicationId;

    public AssessmentFormController() {}

    public void setServices(RecruiterService recruiterService, AssessmentService assessmentService) {
        this.recruiterService = recruiterService;
        this.assessmentService = assessmentService;
    }

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
            String techStr = technicalScoreField.getText().trim();
            String hrStr = hrScoreField.getText().trim();

            // Required field check
            if (techStr.isEmpty() || hrStr.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Technical and HR scores are required!").show();
                return;
            }

            // Numeric validation
            double technical = Double.parseDouble(techStr);
            double hr = Double.parseDouble(hrStr);

            // Range validation
            if (technical < 0 || technical > 100) {
                new Alert(Alert.AlertType.ERROR, "Technical score must be between 0 and 100!").show();
                return;
            }
            if (hr < 0 || hr > 100) {
                new Alert(Alert.AlertType.ERROR, "HR score must be between 0 and 100!").show();
                return;
            }

            String remarks = remarksArea.getText().trim();
            if (!remarks.isEmpty() && remarks.length() < 5) {
                new Alert(Alert.AlertType.WARNING, "Remarks must be at least 5 characters if entered!").show();
                return;
            }

            int recruiterId = SessionManager.loggedInUser.getId();

            AssessmentResult result = new AssessmentResult(shortlistId, recruiterId, technical, hr, remarks);
            assessmentService.recordAssessment(result);

            new Alert(Alert.AlertType.INFORMATION, "Assessment saved successfully!").showAndWait();
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
