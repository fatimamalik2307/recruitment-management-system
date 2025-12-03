package com.recruitment.app.controllers;

import com.recruitment.app.services.*;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class HiringManagerDashboardController {

    // Services (will be injected by ControllerFactory)
    private RecruiterService recruiterService;
    private HMService hmService;
    private NoteService noteService;
    private JobService jobService;
    private UserService userService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public HiringManagerDashboardController() {
        // Empty - services will be injected via setter
    }

    // ---------- SERVICE INJECTION ----------
    public void setServices(
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
            // Use SceneLoader with DI
            Stage stage = new Stage();
            SceneLoader.loadWithDIAndConfig(stage, "/ui/HMCandidateReview.fxml", controller -> {
                if (controller instanceof HMCandidateReviewController) {
                    HMCandidateReviewController reviewController = (HMCandidateReviewController) controller;
                    // Set dynamic runtime data
                    reviewController.setCurrentUserId(getCurrentUserId());
                    reviewController.loadInitialData();
                }
            });
            stage.setTitle("Review Candidates");

        } catch (Exception e) {
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
            Stage stage = new Stage();
            SceneLoader.loadWithDIAndConfig(stage, "/ui/HMJobPostings.fxml", controller -> {
                if (controller instanceof HMJobPostingsController) {
                    HMJobPostingsController jobsController = (HMJobPostingsController) controller;
                    jobsController.setCurrentUserId(getCurrentUserId());
                }
            });
            stage.setTitle("My Job Postings");

        } catch (Exception e) {
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
            Stage stage = new Stage();
            SceneLoader.loadWithDI(stage, "/ui/profile.fxml", "Update Profile");

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
            Stage stage = new Stage();
            SceneLoader.loadWithDI(stage, "/ui/change_password.fxml", "Change Password");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Cannot open change password screen");
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
            // Use DI for login too (if LoginController needs services)
            Stage loginStage = new Stage();
            SceneLoader.loadWithDI(loginStage, "/ui/Login.fxml", "Login");

        } catch (Exception e) {
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