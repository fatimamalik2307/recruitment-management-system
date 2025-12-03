package com.recruitment.app.controllers;

import com.recruitment.app.services.*;
import com.recruitment.app.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class HiringManagerDashboardController {

    // Services only (injected)
    private final RecruiterService recruiterService;
    private final HMService hmService;
    private final NoteService noteService;
    private final JobService jobService;
    private final UserService userService;

    // Constructor injection ensures proper DI
    public HiringManagerDashboardController(
            RecruiterService recruiterService,
            HMService hmService,
            NoteService noteService,
            JobService jobService,
            UserService userService
    ) {
        this.recruiterService = recruiterService;
        this.hmService = hmService;
        this.noteService = noteService;
        this.jobService = jobService;
        this.userService = userService;
    }

    // -----------------------
    // Candidate Review
    // -----------------------
    @FXML
    private void openHMCandidateReview(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/HMCandidateReview.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Review Candidates");

            HMCandidateReviewController controller = loader.getController();

            // Inject required services
            controller.setHMService(hmService);
            controller.setNoteService(noteService);
            controller.setRecruiterService(recruiterService);
            controller.setUserService(userService);

            // Set current user ID from session
            controller.setCurrentUserId(getCurrentUserId());

            // Load initial data after injecting services & user ID
            controller.loadInitialData();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to open Candidate Review.");
        }
    }

    // -----------------------
    // Job Postings (optional)
    // -----------------------
    /*
    @FXML
    private void openJobPostings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/HMJobPostings.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("My Job Postings");

            HMJobPostingsController controller = loader.getController();
            controller.setJobService(jobService);
            controller.setHmService(hmService);
            controller.setCurrentUserId(getCurrentUserId());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to open Job Postings.");
        }
    }
    */

    // -----------------------
    // Selected Candidates view
    // -----------------------
    @FXML
    private void openSelectedCandidates(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION,
                "Selected Candidates view is integrated into the Review Candidates section.");
        // Optionally: openHMCandidateReview(event);
    }

    // -----------------------
    // Profile
    // -----------------------
    @FXML
    private void openProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/profile.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Update Profile");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("⚠ ERROR: Could not load profile.fxml");
        }
    }

    // -----------------------
    // Change Password
    // -----------------------
    @FXML
    private void openChangePassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/change_password.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Change Password");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------
    // Logout
    // -----------------------
    @FXML
    private void logout(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(loader.load()));
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------
    // Helpers
    // -----------------------
    private int getCurrentUserId() {
        return SessionManager.loggedInUser.getId();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}
