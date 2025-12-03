package com.recruitment.app.controllers;

import com.recruitment.app.di.ControllerFactory;
import com.recruitment.app.services.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class RecruiterDashboardController {

    private JobService jobService;
    private ShortlistingCriteriaService criteriaService;
    private ShortlistService shortlistService;
    private RecruiterService recruiterService;
    private AssessmentService assessmentService;
    private FinalRankingService finalRankingService;
    private HMService hmService;

    // NO-ARG constructor required for FXMLLoader
    public RecruiterDashboardController() { }

    // Setter method to inject services (KEEP THIS - ControllerFactory uses it)
    public void setServices(
            JobService jobService,
            ShortlistingCriteriaService criteriaService,
            ShortlistService shortlistService,
            RecruiterService recruiterService,
            AssessmentService assessmentService,
            FinalRankingService finalRankingService,
            HMService hmService
    ) {
        this.jobService = jobService;
        this.criteriaService = criteriaService;
        this.shortlistService = shortlistService;
        this.recruiterService = recruiterService;
        this.assessmentService = assessmentService;
        this.finalRankingService = finalRankingService;
        this.hmService = hmService;
    }


    // -----------------------
    // Open Create Job Posting
    // -----------------------
    @FXML
    private void openCreateJob(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/create_job_posting.fxml"));

            // USE ControllerFactory instead of manually setting service
            loader.setControllerFactory(ControllerFactory.getInstance());

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Create Job Posting");
            stage.showAndWait();

            // REMOVED: controller.setJobService(jobService); // No longer needed

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------
    // Review Applications
    // -----------------------
    @FXML
    private void openReviewApplications(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ReviewApplications.fxml"));

            // USE ControllerFactory
            loader.setControllerFactory(ControllerFactory.getInstance());

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Review Applications");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------
    // Review Shortlist
    // -----------------------
    @FXML
    private void openReviewShortlist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ReviewShortlist.fxml"));

            // USE ControllerFactory instead of manual service injection
            loader.setControllerFactory(ControllerFactory.getInstance());

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Review Shortlist");
            stage.show();

            // REMOVED: Manual service injection - ControllerFactory handles it

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------
    // Shortlisting Criteria
    // -----------------------
    @FXML
    private void openShortlistingCriteria(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ShortlistingCriteria.fxml"));

            // USE ControllerFactory
            loader.setControllerFactory(ControllerFactory.getInstance());

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Define Shortlisting Criteria");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------
    // Recruitment Report
    // -----------------------
    @FXML
    private void openRecruitmentReport(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/recruitment_report_view.fxml"));

            // USE ControllerFactory
            loader.setControllerFactory(ControllerFactory.getInstance());

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Recruitment Report");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Recruitment Report window.").show();
        }
    }

    // -----------------------
    // Final Ranking
    // -----------------------
    @FXML
    private void openFinalRanking(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/FinalRanking.fxml"));

            // USE ControllerFactory
            loader.setControllerFactory(ControllerFactory.getInstance());

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Final Ranking");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Final Ranking window.").show();
        }
    }

    // -----------------------
    // Profile
    // -----------------------
    @FXML
    private void openProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/profile.fxml"));

            // USE ControllerFactory
            loader.setControllerFactory(ControllerFactory.getInstance());

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

            // USE ControllerFactory
            loader.setControllerFactory(ControllerFactory.getInstance());

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Change Password");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Cannot open change password screen").show();
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

            // USE ControllerFactory for Login too (if LoginController needs services)
            // loader.setControllerFactory(ControllerFactory.getInstance());

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(loader.load()));
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}