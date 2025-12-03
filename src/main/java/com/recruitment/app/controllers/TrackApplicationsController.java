package com.recruitment.app.controllers;

import com.recruitment.app.models.Application;
import com.recruitment.app.services.ApplicationService;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.beans.property.SimpleStringProperty;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TrackApplicationsController {

    @FXML private TableView<Application> applicationsTable;
    @FXML private TableColumn<Application, String> jobCol;
    @FXML private TableColumn<Application, String> dateCol;
    @FXML private TableColumn<Application, String> statusCol;

    // Service will be injected by ControllerFactory
    private ApplicationService applicationService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public TrackApplicationsController() {
        // Empty - service will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @FXML
    public void initialize() {
        // ADD null check for service
        if (applicationService == null) {
            System.err.println("ApplicationService not injected!");
            return;
        }

        int userId = SessionManager.loggedInUser.getId();

        // Use injected service instead of direct DAO
        List<Application> apps = applicationService.getByUser(userId);

        jobCol.setCellValueFactory(data ->
                new SimpleStringProperty(applicationService.getJobTitle(data.getValue().getJobId())));

        dateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAppliedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));

        applicationsTable.getItems().setAll(apps);
    }

    @FXML
    public void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Use DI method for navigation
        SceneLoader.loadWithDI(stage, "/ui/browse_jobs.fxml", "Browse Jobs");
    }
}