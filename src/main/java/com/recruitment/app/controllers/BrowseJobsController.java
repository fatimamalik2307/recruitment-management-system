package com.recruitment.app.controllers;

import com.recruitment.app.dao.JobDAOImpl;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.utils.SceneLoader;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

public class BrowseJobsController {

    @FXML private TableView<JobPosting> jobsTable;
    @FXML private TableColumn<JobPosting, String> titleCol;
    @FXML private TableColumn<JobPosting, String> deptCol;
    @FXML private TableColumn<JobPosting, String> deadlineCol;
    @FXML private TableColumn<JobPosting, Void> actionCol;

    private JobDAOImpl jobDAO = new JobDAOImpl();

    @FXML
    public void initialize() {

        // Mapping table columns
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getJobTitle()));
        deptCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDepartment()));
        deadlineCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDeadline()));

        // Add action button
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
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });

        // Load jobs from DB
        jobsTable.getItems().setAll(jobDAO.getAllJobs());
    }

    private void openJobDetails(JobPosting job, javafx.event.ActionEvent event) {
        JobDetailsController.selectedJob = job;
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/job_details.fxml");
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
}
