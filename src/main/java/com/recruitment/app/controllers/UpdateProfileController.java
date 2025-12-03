package com.recruitment.app.controllers;

import com.recruitment.app.models.User;
import com.recruitment.app.services.UserService;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

public class UpdateProfileController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField contactField;
    @FXML private Label messageLabel;

    // Service will be injected by ControllerFactory
    private UserService userService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public UpdateProfileController() {
        // Empty - service will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        if (SessionManager.loggedInUser == null) {
            messageLabel.setText("Error: No user session found");
            return;
        }

        User user = SessionManager.loggedInUser;

        fullNameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        contactField.setText(user.getContact());
    }

    @FXML
    public void updateProfile(ActionEvent event) {
        // ADD null check for service
        if (userService == null) {
            messageLabel.setText("System error: Services not initialized!");
            return;
        }

        User user = SessionManager.loggedInUser;

        user.setFullName(fullNameField.getText());
        user.setEmail(emailField.getText());
        user.setContact(contactField.getText());

        boolean updated = userService.updateProfile(user);

        if (updated) {
            messageLabel.setText("Profile updated!");
        } else {
            messageLabel.setText("Update failed.");
        }
    }

    @FXML
    public void backToJobs(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Use DI method for navigation
        SceneLoader.loadWithDI(stage, "/ui/browse_jobs.fxml", "Browse Jobs");
    }
}