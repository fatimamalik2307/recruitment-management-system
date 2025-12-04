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

    private UserService userService;

    public UpdateProfileController() {}


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

        if (userService == null) {
            messageLabel.setText("System error: Services not initialized!");
            return;
        }


        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            messageLabel.setText("Invalid email format!");
            return;
        }


        if (!contact.matches("\\d+")) {
            messageLabel.setText("Contact number must contain digits only!");
            return;
        }


        if (contact.length() != 11) {
            messageLabel.setText("Contact number must be 11 digits!");
            return;
        }

        User user = SessionManager.loggedInUser;

        user.setFullName(fullName);
        user.setEmail(email);
        user.setContact(contact);

        boolean updated = userService.updateProfile(user);

        if (updated) {
            messageLabel.setText("Profile updated successfully!");
        } else {
            messageLabel.setText("Update failed.");
        }
    }

    @FXML
    public void backToJobs(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        String role = SessionManager.loggedInUser.getRole(); // assuming role is saved

        switch (role.toLowerCase()) {
            case "user":
            case "applicant":
                SceneLoader.loadWithDI(stage, "/ui/browse_jobs.fxml", "Browse Jobs");
                break;

            case "recruiter":
                SceneLoader.loadWithDI(stage, "/ui/RecruiterDashboard.fxml", "Recruiter Dashboard");
                break;

            case "hiring manager":
                SceneLoader.loadWithDI(stage, "/ui/HiringManagerDashboard.fxml", "Hiring Manager Dashboard");
                break;

            default:
                SceneLoader.loadWithDI(stage, "/ui/browse_jobs.fxml", "Browse Jobs");
                break;
        }
    }
    }
