package com.recruitment.app.controllers;

import com.recruitment.app.config.DBConnection;
import com.recruitment.app.dao.*;
import com.recruitment.app.models.ShortlistingCriteria;
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

    // DAOs
    private final JobDAO jobDAO = new JobDAOImpl(DBConnection.getConnection());
    private final ApplicationDAO applicationDAO = new ApplicationDAOImpl(DBConnection.getConnection());
    private final FinalRankedCandidateDAO candidateDAO = new FinalRankedCandidateDAOImpl(DBConnection.getConnection());
    private final ApplicantNoteDAO noteDAO = new ApplicantNoteDAOImpl(DBConnection.getConnection());
    private  final ShortlistingCriteriaDAO criteriaDAO = new ShortlistingCriteriaDAOImpl(DBConnection.getConnection());
    // Services
    private final RecruiterService recruiterService = new RecruiterServiceImpl(jobDAO,applicationDAO,criteriaDAO);
    private final HMService hmService = new HMServiceImpl(candidateDAO, applicationDAO, jobDAO);
    private final NoteService noteService = new NoteServiceImpl(noteDAO);
    private final JobService jobService = new JobServiceImpl(jobDAO);
    private final UserService userService = new UserService();

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

            controller.setCurrentUserId((int) getCurrentUserId());

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

    // ADD THIS MISSING METHOD
    @FXML
    private void openSelectedCandidates(ActionEvent event) {
        // For now, just show a message since we removed this feature
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
        // Get the actual logged-in user ID from session
        return SessionManager.loggedInUser.getId();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}