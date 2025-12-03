package com.recruitment.app.utils;

import com.recruitment.app.controllers.ApplicationFormController;
import com.recruitment.app.di.ControllerFactory;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.ApplicationService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class SceneLoader {

    private static final ControllerFactory controllerFactory = ControllerFactory.getInstance();

    // ============== KEEP ALL EXISTING METHODS FOR BACKWARD COMPATIBILITY ==============

    // Basic load method for simple cases (UNCHANGED except setting controllerFactory)
    public static void load(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));

            // <-- ensure controllers created by FXMLLoader use our ControllerFactory
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load with data configuration (for controllers with setters) (UNCHANGED except setting controllerFactory)
    public static void loadWithData(Stage stage, String fxmlPath, Consumer<Object> controllerConfigurer) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));

            // <-- ensure controllers created by FXMLLoader use our ControllerFactory
            loader.setControllerFactory(controllerFactory);

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

    // ============== UPDATE THIS METHOD TO USE CONTROLLERFACTORY ==============
    public static void loadApplicationForm(Stage stage, String fxmlPath,
                                           ApplicationService applicationService, // Keep parameter for compatibility
                                           JobPosting job) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));

            // USE ControllerFactory instead of manual controller creation
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            stage.setScene(new Scene(root));

            // Get the controller (services already injected by ControllerFactory)
            ApplicationFormController controller = loader.getController();

            // Still need to set the dynamic job parameter
            controller.setJob(job);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============== ADD THESE NEW METHODS FOR DI ==============

    /**
     * NEW: Load FXML with Dependency Injection (ControllerFactory)
     * Use this for controllers that need services injected
     */
    public static void loadWithDI(Stage stage, String fxmlPath) {
        loadWithDI(stage, fxmlPath, null);
    }

    /**
     * NEW: Load FXML with Dependency Injection and custom title
     */
    public static void loadWithDI(Stage stage, String fxmlPath, String title) {
        try {
            System.out.println("SceneLoader (DI): Loading " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            stage.setScene(new Scene(root));

            if (title != null) {
                stage.setTitle(title);
            }
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * NEW: Load FXML with DI and additional controller configuration
     * Useful when you need to pass extra data to controller
     */
    public static void loadWithDIAndConfig(Stage stage, String fxmlPath,
                                           Consumer<Object> controllerConfigurer) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            Object controller = loader.getController();

            // Apply additional configuration if provided
            if (controllerConfigurer != null && controller != null) {
                controllerConfigurer.accept(controller);
            }

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get controller factory instance (for use in other controllers)
     */
    public static ControllerFactory getControllerFactory() {
        return controllerFactory;
    }
}