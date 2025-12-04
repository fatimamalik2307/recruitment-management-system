package com.recruitment.app.utils;

import com.recruitment.app.controllers.ApplicationFormController;
import com.recruitment.app.di.ControllerFactory;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.ApplicationService;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class SceneLoader {

    private static final ControllerFactory controllerFactory = ControllerFactory.getInstance();
    private static final String GLOBAL_CSS_PATH = "/styles/style.css"; // Ensure this matches your CSS location

    /**
     * Creates a Scene that fills the screen resolution with the global stylesheet applied
     */
    private static Scene createStyledScene(Parent root) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        Scene scene = new Scene(root, screenWidth, screenHeight);

        try {
            String css = SceneLoader.class.getResource(GLOBAL_CSS_PATH).toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Error loading global stylesheet: " + GLOBAL_CSS_PATH);
            e.printStackTrace();
        }

        return scene;
    }

    // ================== BASIC LOAD METHODS ==================

    public static void load(Stage stage, String fxmlPath) {
        load(stage, fxmlPath, null, true);
    }

    public static void load(Stage stage, String fxmlPath, String title, boolean resizable) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            stage.setScene(createStyledScene(root));
            stage.setResizable(resizable);

            if (title != null) stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== LOAD WITH CONTROLLER CONFIG ==================

    public static void loadWithData(Stage stage, String fxmlPath, Consumer<Object> controllerConfigurer) {
        loadWithData(stage, fxmlPath, controllerConfigurer, null, true);
    }

    public static void loadWithData(Stage stage, String fxmlPath, Consumer<Object> controllerConfigurer, String title, boolean resizable) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            Object controller = loader.getController();
            if (controllerConfigurer != null && controller != null) {
                controllerConfigurer.accept(controller);
            }

            stage.setScene(createStyledScene(root));
            stage.setResizable(resizable);
            if (title != null) stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== APPLICATION FORM LOAD ==================

    public static void loadApplicationForm(Stage stage, String fxmlPath,
                                           ApplicationService applicationService,
                                           JobPosting job) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            ApplicationFormController controller = loader.getController();
            controller.setJob(job); // dynamic data

            stage.setScene(createStyledScene(root));
            stage.setResizable(true); // allow resizing if needed
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== DEPENDENCY INJECTION LOAD ==================

    public static void loadWithDI(Stage stage, String fxmlPath) {
        loadWithDI(stage, fxmlPath, null, true);
    }

    public static void loadWithDI(Stage stage, String fxmlPath, String title) {
        loadWithDI(stage, fxmlPath, title, true);
    }

    public static void loadWithDI(Stage stage, String fxmlPath, String title, boolean resizable) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            stage.setScene(createStyledScene(root));
            stage.setResizable(resizable);

            if (title != null) stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadWithDIAndConfig(Stage stage, String fxmlPath,
                                           Consumer<Object> controllerConfigurer) {
        loadWithDIAndConfig(stage, fxmlPath, controllerConfigurer, null, true);
    }

    public static void loadWithDIAndConfig(Stage stage, String fxmlPath,
                                           Consumer<Object> controllerConfigurer,
                                           String title,
                                           boolean resizable) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            Object controller = loader.getController();

            if (controllerConfigurer != null && controller != null) {
                controllerConfigurer.accept(controller);
            }

            stage.setScene(createStyledScene(root));
            stage.setResizable(resizable);

            if (title != null) stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== CONTROLLER FACTORY ACCESS ==================

    public static ControllerFactory getControllerFactory() {
        return controllerFactory;
    }
}
