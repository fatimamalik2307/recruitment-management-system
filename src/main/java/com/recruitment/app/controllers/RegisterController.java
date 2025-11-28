package com.recruitment.app.controllers;

import com.recruitment.app.models.User;
import com.recruitment.app.services.UserService;
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
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    public void registerUser(ActionEvent event) {
        try {
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                messageLabel.setText("Passwords do not match!");
                return;
            }

            User user = new User(
                    fullNameField.getText(),
                    emailField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    contactField.getText()
            );

            boolean success = userService.register(user);

            if (success) {
                messageLabel.setText("Registration successful!");

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                SceneLoader.load(stage, "/ui/login.fxml");
            } else {
                messageLabel.setText("Registration failed!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error occurred!");
        }
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/login.fxml");
    }

    // If FXML still calls openLogin:
    @FXML
    public void openLogin(ActionEvent event) {
        goToLogin(event);
    }
}
