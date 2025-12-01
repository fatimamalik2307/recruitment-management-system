package com.recruitment.app.controllers;

import com.recruitment.app.dao.JobDescriptionDAO;
import com.recruitment.app.dao.JobDescriptionDAOImpl;
import com.recruitment.app.models.JobDescription;
import com.recruitment.app.utils.DBConnection;
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

    @FXML
    public void initialize() {
        titleCol.setCellValueFactory(c -> c.getValue().titleProperty());
        dateCol.setCellValueFactory(c -> c.getValue().createdAtProperty().asString());

        JobDescriptionDAO dao = new JobDescriptionDAOImpl(DBConnection.getConnection());
        table.getItems().setAll(dao.getByRecruiter(SessionManager.loggedInUser.getId()));
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
