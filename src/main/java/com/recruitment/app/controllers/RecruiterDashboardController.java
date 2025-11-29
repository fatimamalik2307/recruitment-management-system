package com.recruitment.app.controllers;

import com.recruitment.app.dao.JobDAO;
import com.recruitment.app.dao.JobDAOImpl;
import com.recruitment.app.services.JobService;
import com.recruitment.app.services.JobServiceImpl;
import com.recruitment.app.config.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class RecruiterDashboardController {

    // Use your DBConnection
    JobDAO jobDAO = new JobDAOImpl(DBConnection.getConnection());
    private final JobService jobService = new JobServiceImpl(jobDAO);

    @FXML
    private void openCreateJob(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/create_job_posting.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Create Job Posting");

            // Inject JobService into the controller
            CreateJobPostingController controller = loader.getController();
            controller.setJobService(jobService);

            stage.showAndWait(); // Wait until user closes the create job window
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
