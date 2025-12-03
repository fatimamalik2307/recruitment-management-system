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

    // Constructor injection
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

    @FXML
    private void openHMCandidateReview(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/HMCandidateReview.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Review Candidates");

            HMCandidateReviewController controller = loader.getController();

            // Inject ALL required services
            controller.setHMService(hmService);
            controller.setNoteService(noteService);
            controller.setRecruiterService(recruiterService);
            controller.setUserService(userService);

            controller.setCurrentUserId(getCurrentUserId());

            // VERY IMPORTANT: call this after injecting services & user ID
            controller.loadInitialData();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to open Candidate Review.");
        }
    }

//    @FXML
//    private void openJobPostings(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/HMJobPostings.fxml"));
//            Stage stage = new Stage();
//            stage.setScene(new Scene(loader.load()));
//            stage.setTitle("My Job Postings");
//
//            HMJobPostingsController controller = loader.getController();
//            controller.setJobService(jobService);
//            controller.setHmService(hmService);
//            controller.setCurrentUserId(getCurrentUserId());
//
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Failed to open Job Postings.");
//        }
//    }

    @FXML
    private void openSelectedCandidates(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Selected Candidates view is integrated into the Review Candidates section.");
        // Optionally, you can open the candidate review instead
        // openHMCandidateReview(event);
    }

    @FXML
    private void openProfile(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile window not implemented yet.", ButtonType.OK);
        alert.showAndWait();
    }

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

    private int getCurrentUserId() {
        return SessionManager.loggedInUser.getId();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}
