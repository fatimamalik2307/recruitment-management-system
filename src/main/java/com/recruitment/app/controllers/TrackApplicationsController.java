package com.recruitment.app.controllers;

import com.recruitment.app.dao.ApplicationDAO;
import com.recruitment.app.dao.ApplicationDAOImpl;
import com.recruitment.app.models.Application;
import com.recruitment.app.utils.SessionManager;
import com.recruitment.app.utils.DBConnection;
import com.recruitment.app.utils.SceneLoader;

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

    @FXML
    public void initialize() {
        int userId = SessionManager.loggedInUser.getId();

        ApplicationDAO dao = new ApplicationDAOImpl(DBConnection.getConnection());
        List<Application> apps = dao.getApplicationsByUserId(userId);

        jobCol.setCellValueFactory(data ->
                new SimpleStringProperty(dao.getJobTitle(data.getValue().getJobId())));

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
        SceneLoader.load(stage, "/ui/browse_jobs.fxml");
    }
}
