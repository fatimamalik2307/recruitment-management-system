package com.recruitment.app.controllers;

import com.recruitment.app.models.*;
import com.recruitment.app.services.UserService;
import com.recruitment.app.services.UserService.RegistrationResult;
import com.recruitment.app.utils.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField contactField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField companyField;
    @FXML private Label messageLabel;

    private UserService userService;

    public RegisterController() {}

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        // Populate roles
        roleCombo.getItems().addAll("Applicant", "Recruiter", "Hiring Manager");
        roleCombo.setValue("Applicant");

        // Show/hide company field
        roleCombo.setOnAction(e -> {
            String role = roleCombo.getValue();
            companyField.setDisable(role.equalsIgnoreCase("Applicant"));
            companyField.setVisible(!role.equalsIgnoreCase("Applicant"));
            if (role.equalsIgnoreCase("Applicant")) companyField.clear();
        });

        companyField.setDisable(true);
        companyField.setVisible(false);
    }

    @FXML
    public void registerUser(ActionEvent event) {
        try {
            if (userService == null) {
                messageLabel.setText("System error: Services not initialized!");
                return;
            }

            // Basic validation
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
            String companyName = companyField.getText().trim();
            User user;

            // Create appropriate subclass
            switch (role) {
                case "Recruiter":
                    if (companyName.isEmpty()) {
                        messageLabel.setText("Company name is required for Recruiter");
                        return;
                    }
                    user = new Recruiter(
                            fullNameField.getText().trim(),
                            emailField.getText().trim(),
                            usernameField.getText().trim(),
                            passwordField.getText(),
                            contactField.getText().trim()
                    );
                    break;

                case "Hiring Manager":
                    if (companyName.isEmpty()) {
                        messageLabel.setText("Company name is required for Hiring Manager");
                        return;
                    }
                    user = new HiringManager(
                            fullNameField.getText().trim(),
                            emailField.getText().trim(),
                            usernameField.getText().trim(),
                            passwordField.getText(),
                            contactField.getText().trim()
                    );
                    break;

                default: // Applicant
                    user = new Applicant(
                            fullNameField.getText().trim(),
                            emailField.getText().trim(),
                            usernameField.getText().trim(),
                            passwordField.getText(),
                            contactField.getText().trim()
                    );
            }

            // Unified registration call
            RegistrationResult result = userService.registerUser(user, companyName);
            messageLabel.setText(result.getMessage());
            if (result.isSuccess()) gotoLogin(event);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error occurred during registration.");
        }
    }

    private void gotoLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.loadWithDI(stage, "/ui/login.fxml", "Login");
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        gotoLogin(event);
    }
}
