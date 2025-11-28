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

    private final UserService userService = new UserService();

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
        SceneLoader.load(stage, "/ui/browse_jobs.fxml");
    }
}
