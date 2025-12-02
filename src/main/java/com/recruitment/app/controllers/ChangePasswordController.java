package com.recruitment.app.controllers;

import com.recruitment.app.models.User;
import com.recruitment.app.services.UserService;
import com.recruitment.app.services.UserServiceImpl;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

public class ChangePasswordController {

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserServiceImpl();


    @FXML
    public void updatePassword(ActionEvent event) {
        User user = SessionManager.loggedInUser;

        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (!userService.checkPassword(oldPass, user.getPassword())) {
            messageLabel.setText("Old password is incorrect!");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            messageLabel.setText("Passwords do not match!");
            return;
        }

        boolean updated = userService.updatePassword(user.getId(), newPass);

        if (updated) {
            messageLabel.setText("Password updated successfully!");

            // update session copy
            user.setPassword(userService.hashPassword(newPass));
        } else {
            messageLabel.setText("Error updating password!");
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}



