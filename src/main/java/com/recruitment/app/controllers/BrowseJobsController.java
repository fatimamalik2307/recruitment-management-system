package com.recruitment.app.controllers;

import com.recruitment.app.config.DBConnection;
import com.recruitment.app.dao.JobDAOImpl;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.utils.SceneLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.Connection;

public class BrowseJobsController {

    @FXML private TableView<JobPosting> jobsTable;
    @FXML private TableColumn<JobPosting, String> titleCol;
    @FXML private TableColumn<JobPosting, String> deptCol;
    @FXML private TableColumn<JobPosting, String> deadlineCol;
    @FXML private TableColumn<JobPosting, Void> actionCol;

    private JobDAOImpl jobDAO = new JobDAOImpl(DBConnection.getConnection());

    @FXML
    public void initialize() {
        // Map table columns
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        deptCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment()));
        deadlineCol.setCellValueFactory(data -> {
            if (data.getValue().getDeadline() != null) {
                return new SimpleStringProperty(data.getValue().getDeadline().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        // Action button
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View");

            {
                btn.setOnAction(event -> {
                    JobPosting job = getTableView().getItems().get(getIndex());
                    openJobDetails(job, event);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Load jobs from DB
        jobsTable.getItems().setAll(jobDAO.getAllJobs());
    }

    private void openJobDetails(JobPosting job, javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/job_details.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            JobDetailsController controller = loader.getController();
            controller.setJob(job); // pass the job object

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void logout(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/login.fxml");
    }

    @FXML
    public void openProfile(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/profile.fxml");
    }
    @FXML
    public void openTrackApplications(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/track_applications.fxml");
    }
    @FXML
    public void openChangePassword(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/change_password.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Change Password");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" ERROR: Could not load change_password.fxml");
        }
    }

}
