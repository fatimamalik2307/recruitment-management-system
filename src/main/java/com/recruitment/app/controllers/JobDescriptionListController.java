package com.recruitment.app.controllers;

import com.recruitment.app.models.JobDescription;
import com.recruitment.app.services.JobDescriptionService;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class JobDescriptionListController {

    public static String caller = "create_job";

    @FXML private TableView<JobDescription> table;
    @FXML private TableColumn<JobDescription, String> titleCol;
    @FXML private TableColumn<JobDescription, String> dateCol;

    // Service will be injected by ControllerFactory
    private JobDescriptionService service;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public JobDescriptionListController() {
        // Empty - service will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setJobDescriptionService(JobDescriptionService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        titleCol.setCellValueFactory(c -> c.getValue().titleProperty());
        dateCol.setCellValueFactory(c -> c.getValue().createdAtProperty().asString());

        // ADD null check for service
        if (service != null) {
            table.getItems().setAll(service.getByRecruiter(SessionManager.loggedInUser.getId()));
        } else {
            System.err.println("JobDescriptionService not injected!");
            // You could show an error message to the user
            new Alert(Alert.AlertType.ERROR, "Service not initialized").show();
        }
    }

    @FXML
    private void loadSelected() {
        JobDescription selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Stage stage = (Stage) table.getScene().getWindow();

        if ("edit_description".equals(caller)) {
            JobDescriptionController.prefill(selected);
            SceneLoader.load(stage, "/ui/job_description.fxml");
        } else {
            CreateJobPostingController.prefillFromDescription(selected);
            SceneLoader.load(stage, "/ui/create_job_posting.fxml");
        }
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) table.getScene().getWindow();
        SceneLoader.load(stage, "/ui/create_job_posting.fxml");
    }
}