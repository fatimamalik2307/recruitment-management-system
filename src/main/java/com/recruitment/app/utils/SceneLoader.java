package com.recruitment.app.utils;

import com.recruitment.app.controllers.ApplicationFormController;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.ApplicationService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class SceneLoader {

    // Basic load method for simple cases
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

    // Load with data configuration (for controllers with setters)
    public static void loadWithData(Stage stage, String fxmlPath, Consumer<Object> controllerConfigurer) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controllerConfigurer != null && controller != null) {
                controllerConfigurer.accept(controller);
            }
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Keep your existing code working - ADD this new method
    public static void loadApplicationForm(Stage stage, String fxmlPath,
                                           ApplicationService applicationService,
                                           JobPosting job) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));

            // Set controller factory
            loader.setControllerFactory(controllerClass -> {
                try {
                    // For ApplicationFormController
                    if (ApplicationFormController.class.equals(controllerClass)) {
                        ApplicationFormController controller = new ApplicationFormController();
                        // Inject dependencies
                        controller.setApplicationService(applicationService);
                        controller.setJob(job); // job is already JobPosting type
                        return controller;
                    }
                    // For other controllers
                    return controllerClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}