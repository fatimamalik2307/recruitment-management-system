package com.recruitment.app.utils;

import com.recruitment.app.controllers.ApplicationFormController;
import com.recruitment.app.controllers.JobDetailsController;
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
    private static final String GLOBAL_CSS_PATH = "/styles/style.css"; // Ensure this matches your CSS location

    // Helper method to create a scene and apply the global stylesheet
    private static Scene createStyledScene(Parent root) {
        Scene scene = new Scene(root);
        try {
            // Load the CSS file
            String css = SceneLoader.class.getResource(GLOBAL_CSS_PATH).toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Error loading global stylesheet: " + GLOBAL_CSS_PATH);
            e.printStackTrace();
        }
        return scene;
    }

    // ============== KEEP ALL EXISTING METHODS FOR BACKWARD COMPATIBILITY ==============

    // Basic load method for simple cases
    public static void load(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));

            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            stage.setScene(createStyledScene(root));
            stage.setMaximized(true); // <-- Using the new helper
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load with data configuration (for controllers with setters)
    public static void loadWithData(Stage stage, String fxmlPath, Consumer<Object> controllerConfigurer) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));

            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            Object controller = loader.getController();
            if (controllerConfigurer != null && controller != null) {
                controllerConfigurer.accept(controller);
            }
            stage.setScene(createStyledScene(root)); // <-- Using the new helper
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============== UPDATE THIS METHOD TO USE CONTROLLERFACTORY ==============
    public static void loadApplicationForm(Stage stage, String fxmlPath,
                                           ApplicationService applicationService,
                                           JobPosting job) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));

            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();

            // Get the controller (services already injected by ControllerFactory)
            ApplicationFormController controller = loader.getController();

            // Still need to set the dynamic job parameter
            controller.setJob(job);

            stage.setScene(createStyledScene(root));
            stage.setMaximized(true); // <-- Using the new helper
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadJobDetails(Stage stage, JobPosting job) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource("/ui/job_details.fxml"));
            loader.setControllerFactory(ControllerFactory.getInstance());

            Parent root = loader.load();
            JobDetailsController controller = loader.getController();
            controller.setJob(job); // ← inject job again

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============== ADD THESE NEW METHODS FOR DI (UPDATED) ==============

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
            stage.setScene(createStyledScene(root)); // <-- Using the new helper

            if (title != null) {
                stage.setTitle(title);
            }
            stage.setMaximized(true);
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

            stage.setScene(createStyledScene(root));
            stage.setMaximized(true); // <-- Using the new helper
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