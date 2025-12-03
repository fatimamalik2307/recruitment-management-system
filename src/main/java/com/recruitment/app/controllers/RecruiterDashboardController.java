package com.recruitment.app.controllers;

import com.recruitment.app.services.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class RecruiterDashboardController {

    // Services (injected via constructor)
    private final JobService jobService;
    private final ShortlistingCriteriaService criteriaService;
    private final ShortlistService shortlistService;
    private final RecruiterService recruiterService;
    private final AssessmentService assessmentService;
    private final FinalRankingService finalRankingService;
    private final HMService hmService;

    // Constructor injection
    public RecruiterDashboardController(
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
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Create Job Posting");

            CreateJobPostingController controller = loader.getController();
            controller.setJobService(jobService);

            stage.showAndWait();
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
    private void openReviewShortlist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ReviewShortlist.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Review Shortlist");

            ReviewShortlistController controller = loader.getController();
            controller.setServices(shortlistService, criteriaService, recruiterService, assessmentService, finalRankingService);

            stage.show();
        } catch (IOException e) {
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
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Recruitment Report");

            RecruitmentReportController controller = loader.getController();
            // Services handled internally

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
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Final Ranking");

            FinalRankingController controller = loader.getController();
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
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(loader.load()));
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
