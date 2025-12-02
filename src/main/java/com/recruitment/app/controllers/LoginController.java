package com.recruitment.app.controllers;

import com.recruitment.app.models.User;
import com.recruitment.app.services.UserService;
import com.recruitment.app.services.UserServiceImpl;
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

    private final UserService userService = new UserServiceImpl();


    @FXML
    public void loginUser(ActionEvent event) {
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
                    SceneLoader.load(stage, "/ui/RecruiterDashboard.fxml");
                    break;

                case "hiring manager":
                case "hiring_manager":
                case "hiringmanager":
                    SceneLoader.load(stage, "/ui/HiringManagerDashboard.fxml");
                    break;

                case "applicant":
                default:
                    SceneLoader.load(stage, "/ui/browse_jobs.fxml");
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
        SceneLoader.load(stage, "/ui/register.fxml");
    }
}
