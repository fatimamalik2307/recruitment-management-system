package com.recruitment.app.controllers;

import com.recruitment.app.utils.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Node;

public class WelcomeController {

    @FXML
    public void openLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Use DI method for navigation
        SceneLoader.loadWithDI(stage, "/ui/login.fxml", "Login");
    }

    @FXML
    public void openRegister(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Use DI method for navigation
        SceneLoader.loadWithDI(stage, "/ui/register.fxml", "Register");
    }
}