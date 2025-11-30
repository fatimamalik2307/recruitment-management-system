package com.recruitment.app.controllers;

import com.recruitment.app.utils.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;

public class HiringManagerDashboardController {

    @FXML
    public void openShortlistedCandidates(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "Shortlisted candidates screen not implemented yet.",
                ButtonType.OK);
        a.showAndWait();
    }

    @FXML
    public void openAllApplications(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "All applications screen not implemented yet.",
                ButtonType.OK);
        a.showAndWait();
    }

    @FXML
    public void openFinalDecision(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "Final hiring decision screen not implemented.",
                ButtonType.OK);
        a.showAndWait();
    }

    @FXML
    public void logout(ActionEvent event) {
        try {
            Stage current = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();
            current.close();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ui/Login.fxml")
            );

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(loader.load()));
            loginStage.setTitle("Login");
            loginStage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
