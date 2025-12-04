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
        roleCombo.getItems().addAll("Applicant", "Recruiter", "Hiring Manager");
        roleCombo.setValue("Applicant");

        // Show/hide company field
        roleCombo.setOnAction(e -> {
            boolean isApplicant = roleCombo.getValue().equalsIgnoreCase("Applicant");
            companyField.setVisible(!isApplicant);
            companyField.setDisable(isApplicant);
            if (isApplicant) companyField.clear();
        });

        companyField.setVisible(false);
        companyField.setDisable(true);
    }

    // ---------------- VALIDATION HELPERS ---------------- //

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    private void markError(Control c) {
        c.getStyleClass().add("input-field-error");
    }

    private void clearErrors() {
        fullNameField.getStyleClass().remove("input-field-error");
        emailField.getStyleClass().remove("input-field-error");
        usernameField.getStyleClass().remove("input-field-error");
        passwordField.getStyleClass().remove("input-field-error");
        confirmPasswordField.getStyleClass().remove("input-field-error");
        contactField.getStyleClass().remove("input-field-error");
        companyField.getStyleClass().remove("input-field-error");
    }

    // ---------------- REGISTER USER ---------------- //

    @FXML
    public void registerUser(ActionEvent event) {

        clearErrors();
        messageLabel.setText("");

        // --- Required fields check ---
        if (fullNameField.getText().isBlank()) {
            markError(fullNameField);
            messageLabel.setText("Full name is required.");
            return;
        }

        if (!isValidEmail(emailField.getText())) {
            markError(emailField);
            messageLabel.setText("Invalid email format.");
            return;
        }

        if (usernameField.getText().isBlank()) {
            markError(usernameField);
            messageLabel.setText("Username is required.");
            return;
        }

        if (passwordField.getText().isBlank()) {
            markError(passwordField);
            messageLabel.setText("Password is required.");
            return;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            markError(confirmPasswordField);
            messageLabel.setText("Passwords do not match.");
            return;
        }

        // -------- PHONE NUMBER VALIDATION -------- //
        String contact = contactField.getText().trim();

        if (!isNumeric(contact)) {
            markError(contactField);
            messageLabel.setText("Contact must contain numbers only.");
            return;
        }

        if (contact.length() != 11) {
            markError(contactField);
            messageLabel.setText("Contact number must be exactly 11 digits.");
            return;
        }

        // -------- COMPANY VALIDATION -------- //
        String role = roleCombo.getValue();
        String companyName = companyField.getText().trim();

        if ((role.equals("Recruiter") || role.equals("Hiring Manager")) && companyName.isBlank()) {
            markError(companyField);
            messageLabel.setText("Company name is required for this role.");
            return;
        }

        // -------- USER OBJECT CREATION -------- //
        User user;

        switch (role) {
            case "Recruiter":
                user = new Recruiter(
                        fullNameField.getText(),
                        emailField.getText(),
                        usernameField.getText(),
                        passwordField.getText(),
                        contact
                );
                break;

            case "Hiring Manager":
                user = new HiringManager(
                        fullNameField.getText(),
                        emailField.getText(),
                        usernameField.getText(),
                        passwordField.getText(),
                        contact
                );
                break;

            default: // Applicant
                user = new Applicant(
                        fullNameField.getText(),
                        emailField.getText(),
                        usernameField.getText(),
                        passwordField.getText(),
                        contact
                );
        }

        // -------- CALL SERVICE -------- //
        try {
            RegistrationResult result = userService.registerUser(user, companyName);

            if (result.isSuccess()) {
                messageLabel.setStyle("-fx-text-fill: #4fff4f;");
                messageLabel.setText(result.getMessage());
                gotoLogin(event);
            } else {
                messageLabel.setText(result.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unexpected error occurred.");
        }
    }

    // Navigate to login
    private void gotoLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.loadWithDI(stage, "/ui/login.fxml", "Login");
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        gotoLogin(event);
    }
}
