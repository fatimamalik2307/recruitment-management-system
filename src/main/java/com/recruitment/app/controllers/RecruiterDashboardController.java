package com.recruitment.app.controllers;

import com.recruitment.app.config.DBConnection;
import com.recruitment.app.dao.*;
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

    // DAOs
    private final JobDAO jobDAO = new JobDAOImpl(DBConnection.getConnection());
    private final ApplicationDAO applicationDAO = new ApplicationDAOImpl(DBConnection.getConnection());
    private final ShortlistingCriteriaDAO criteriaDAO = new ShortlistingCriteriaDAOImpl(DBConnection.getConnection());
    private final ShortlistDAO shortlistDAO = new ShortlistDAOImpl(DBConnection.getConnection());
    private final AssessmentResultDAO assessmentResultDAO = new AssessmentResultDAOImpl(DBConnection.getConnection());
    private final FinalRankedCandidateDAO candidateDAO = new FinalRankedCandidateDAOImpl(DBConnection.getConnection());
    private final UserDAO userDAO = new UserDAOImpl();
    private final FinalRankingCriteriaDAO criteriaDAO2 = new FinalRankingCriteriaDAOImpl(DBConnection.getConnection());
    // Services
    private final JobService jobService = new JobServiceImpl(jobDAO);
    private final ShortlistingCriteriaService criteriaService = new ShortlistingCriteriaServiceImpl(criteriaDAO);
    private final ShortlistService shortlistService = new ShortlistServiceImpl(shortlistDAO, criteriaDAO, applicationDAO);
    private final RecruiterService recruiterService = new RecruiterServiceImpl(jobDAO, applicationDAO, criteriaDAO);
    private final AssessmentService assessmentService =
            new AssessmentServiceImpl(new AssessmentResultDAOImpl(DBConnection.getConnection()));

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

    @FXML
    private void openReviewShortlist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ReviewShortlist.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Review Shortlist");

            ReviewShortlistController controller = loader.getController();
            // PASS the initialized services
            controller.setServices(shortlistService, criteriaService, recruiterService, assessmentService);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    private void openRecruitmentReport(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/recruitment_report_view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Recruitment Report");

            // Get controller and pass services if needed
            RecruitmentReportController controller = loader.getController();
            // Services are handled inside RecruitmentReportController, so no need to pass anything

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Recruitment Report window.").show();
        }
    }

    @FXML
    private void openFinalRanking(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/FinalRanking.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Final Ranking");

            FinalRankingController controller = loader.getController();

            // Create services
            FinalRankingServiceImpl finalRankingService = new FinalRankingServiceImpl(
                    candidateDAO,
                    criteriaDAO2,
                    assessmentResultDAO,
                    jobDAO,
                    shortlistDAO,
                    userDAO
            );

            HMService hmService = new HMServiceImpl(candidateDAO, applicationDAO, jobDAO);

            // Inject both services
            controller.setFinalRankingService(finalRankingService);
            controller.setHMService(hmService); // Add this line

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Final Ranking window.").show();
        }
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
}
