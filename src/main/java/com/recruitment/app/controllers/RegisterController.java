package com.recruitment.app.controllers;

import com.recruitment.app.models.User;
import com.recruitment.app.models.UserFactory;
import com.recruitment.app.services.UserService;
import com.recruitment.app.services.UserService.RegistrationResult;
import com.recruitment.app.utils.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for registration UI.
 * - Controller handles UI interactions only (SRP).
 * - Delegates business logic to UserService (DIP).
 *
 * FXML fields used (wire these in your register.fxml):
 *   - TextField fullNameField
 *   - TextField emailField
 *   - TextField usernameField
 *   - PasswordField passwordField
 *   - PasswordField confirmPasswordField
 *   - TextField contactField
 *   - ComboBox<String> roleCombo
 *   - TextField companyField  (visible only when role is Recruiter/Hiring Manager)
 *   - Label messageLabel
 */
public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField contactField;

    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField companyField; // used only when role != Applicant
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Populate role dropdown
        roleCombo.getItems().addAll("Applicant", "Recruiter", "Hiring Manager");
        roleCombo.setValue("Applicant");

        // Toggle company field visibility based on role
        roleCombo.setOnAction(e -> {
            String role = roleCombo.getValue();
            companyField.setDisable(role.equalsIgnoreCase("Applicant"));
            companyField.setVisible(!role.equalsIgnoreCase("Applicant"));
            if (role.equalsIgnoreCase("Applicant")) companyField.clear();
        });

        // Initially hide/disable the company field for applicants
        companyField.setDisable(true);
        companyField.setVisible(false);
    }

    @FXML
    public void registerUser(ActionEvent event) {
        try {
            // Basic validations
            if (fullNameField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty() ||
                    usernameField.getText().trim().isEmpty() ||
                    passwordField.getText().trim().isEmpty() ||
                    confirmPasswordField.getText().trim().isEmpty()) {
                messageLabel.setText("Please complete all required fields.");
                return;
            }

            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                messageLabel.setText("Passwords do not match!");
                return;
            }

            String role = roleCombo.getValue();
            String companyName = companyField.getText();

            if (role.equalsIgnoreCase("Applicant")) {
                User user = UserFactory.createApplicant(
                        fullNameField.getText().trim(),
                        emailField.getText().trim(),
                        usernameField.getText().trim(),
                        passwordField.getText(),
                        contactField.getText().trim()
                );

                boolean ok = userService.registerApplicant(user);
                messageLabel.setText(ok ? "Registration successful!" : "Registration failed!");
                if (ok) gotoLogin(event);

            } else {
                // Recruiter or Hiring Manager path
                if (companyName == null || companyName.trim().isEmpty()) {
                    messageLabel.setText("Company name is required for " + role);
                    return;
                }

                User user = UserFactory.createRecruiterOrManager(
                        fullNameField.getText().trim(),
                        emailField.getText().trim(),
                        usernameField.getText().trim(),
                        passwordField.getText(),
                        contactField.getText().trim(),
                        role,
                        null // company id handled by service
                );

                RegistrationResult result = userService.registerRecruiterOrManager(user, companyName.trim());
                messageLabel.setText(result.getMessage());
                if (result.isSuccess()) gotoLogin(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error occurred during registration.");
        }
    }

    private void gotoLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/login.fxml");
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        gotoLogin(event);
    }
}
