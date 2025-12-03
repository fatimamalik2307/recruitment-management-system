package com.recruitment.app.controllers;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.ApplicationService;
import com.recruitment.app.utils.SceneLoader;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadDocumentsController {

    private JobPosting job;
    private List<String> selectedFiles = new ArrayList<>();

    // Service will be injected by ControllerFactory
    private ApplicationService applicationService;

    @FXML
    private ListView<String> filesList;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public UploadDocumentsController() {
        // Empty - service will be injected
    }

    // ---------- SERVICE INJECTION ----------
    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public void setJob(JobPosting job) {
        this.job = job;
    }

    // If user already uploaded before → re-fill list
    public void setPreviousUploadedFiles(List<String> oldFiles) {
        selectedFiles.clear();
        selectedFiles.addAll(oldFiles);
        filesList.getItems().setAll(selectedFiles);
    }

    public List<String> getSelectedFiles() {
        return selectedFiles;
    }

    @FXML
    public void selectDocuments() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Documents");

        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Files", "*.docx")
        );

        List<File> files = fc.showOpenMultipleDialog(null);

        if (files != null) {
            for (File f : files) {
                selectedFiles.add(f.getAbsolutePath());
            }
            filesList.getItems().setAll(selectedFiles);
        }
    }

    // Return to application form and pass selected files
    @FXML
    public void uploadDocuments(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Use DI method for navigation
        SceneLoader.loadWithDIAndConfig(stage, "/ui/application_form.fxml", controller -> {
            if (controller instanceof ApplicationFormController) {
                ApplicationFormController form = (ApplicationFormController) controller;

                // RETURN JOB + FILES (services already injected by ControllerFactory)
                form.setJob(job);
                form.setUploadedFiles(selectedFiles);

                // REMOVED: Manual service injection (now handled by ControllerFactory)
                // form.setApplicationService(...)
            }
        });
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Use DI method for navigation
        SceneLoader.loadWithDI(stage, "/ui/application_form.fxml", "Application Form");
    }
}