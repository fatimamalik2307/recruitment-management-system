package com.recruitment.app.controllers;

import com.recruitment.app.dao.PersonSpecificationDAO;
import com.recruitment.app.dao.PersonSpecificationDAOImpl;
import com.recruitment.app.models.PersonSpecification;
import com.recruitment.app.utils.DBConnection;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PersonSpecificationController {

    @FXML private TextArea skillsArea;
    @FXML private TextArea experienceArea;
    @FXML private TextArea educationArea;
    @FXML private TextArea traitsArea;

    @FXML
    private void saveSpecification() {
        PersonSpecification spec = new PersonSpecification(
                SessionManager.loggedInUser.getId(),
                skillsArea.getText(),
                experienceArea.getText(),
                educationArea.getText(),
                traitsArea.getText()
        );

        new PersonSpecificationDAOImpl(DBConnection.getConnection()).save(spec);
        new Alert(Alert.AlertType.INFORMATION, "Person Specification Saved!", ButtonType.OK).showAndWait();
        Stage stage = (Stage) skillsArea.getScene().getWindow();
          SceneLoader.load(stage, "/ui/create_job_posting.fxml");

    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) skillsArea.getScene().getWindow();
        SceneLoader.load(stage,"/ui/create_job_posting.fxml");
    }
}
