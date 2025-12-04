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

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    // Service will be injected by ControllerFactory
    private UserService userService;

    public LoginController() {
        // Empty - service will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @FXML
    public void loginUser(ActionEvent event) {
        // ADD null check for service
        if (userService == null) {
            messageLabel.setText("System error: Services not initialized!");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = userService.login(username, password);

        if (user == null) {
            messageLabel.setText("Invalid username or password!");
            return;
        }

        // store in session
        SessionManager.loggedInUser = user;

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            String role = user.getRole().toLowerCase().trim();

            switch (role) {
                case "recruiter":
                    // USE DI METHOD
                    SceneLoader.loadWithDI(stage, "/ui/RecruiterDashboard.fxml", "Recruiter Dashboard");
                    break;

                case "hiring manager":
                case "hiring_manager":
                case "hiringmanager":
                    // USE DI METHOD
                    SceneLoader.loadWithDI(stage, "/ui/HiringManagerDashboard.fxml", "Hiring Manager Dashboard");
                    break;

                case "applicant":
                default:
                    // USE DI METHOD
                    SceneLoader.loadWithDI(stage, "/ui/browse_jobs.fxml", "Browse Jobs");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load dashboard!");
        }
    }

    @FXML
    public void openRegister(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Use DI for register too
        SceneLoader.loadWithDI(stage, "/ui/register.fxml", "Register");
    }
}