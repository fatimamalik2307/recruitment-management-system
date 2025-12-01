package com.recruitment.app.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class SceneLoader {

    public static void load(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            Parent root = loader.load();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadWithData(Stage stage, String fxmlPath, Consumer<Object> controllerHandler) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            controllerHandler.accept(controller);

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
