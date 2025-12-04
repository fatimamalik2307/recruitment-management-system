package com.recruitment.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String CSS_PATH = "/styles/style.css"; // Assuming CSS location

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/welcome.fxml"));
        Scene scene = new Scene(loader.load());

        // **********************************************
        // CRITICAL FIX: Load the Stylesheet onto the Scene
        // **********************************************
        String css = getClass().getResource(CSS_PATH).toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Recruitment System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}