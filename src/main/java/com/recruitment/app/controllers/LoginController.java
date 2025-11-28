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

    private final UserService userService = new UserService();

    @FXML
    public void loginUser(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // FIX: remove local var, assign directly
        User user = userService.login(username, password);

        if (user != null) {

            // Store user in global session
            SessionManager.loggedInUser = user;

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneLoader.load(stage, "/ui/browse_jobs.fxml");

        } else {
            messageLabel.setText("Invalid username or password!");
        }
    }

    @FXML
    public void openRegister(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/register.fxml");
    }
}
