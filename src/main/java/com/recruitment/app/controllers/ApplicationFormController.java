package com.recruitment.app.controllers;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.Application;
import com.recruitment.app.services.ApplicationService;
import com.recruitment.app.utils.CVParser;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationFormController {

    private JobPosting job;
    private List<String> uploadedFiles = new ArrayList<>();

    // Service - will be injected by ControllerFactory
    private ApplicationService applicationService;


    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField contactField;
    @FXML private TextArea qualificationField;
    @FXML private TextArea experienceField;
    @FXML private TextArea coverLetterField;
    @FXML private ListView<String> filesList;
    @FXML private Label messageLabel;

    // Buttons
    @FXML private Button btnUpload;
    @FXML private Button btnSubmit;
    @FXML private Button btnBack;

    // ---------- DEFAULT CONSTRUCTOR (KEEP) ----------
    public ApplicationFormController() {
        // Empty - services will be injected
    }

    // ---------- SERVICE INJECTION (SIMPLIFIED) ----------
    public void setApplicationService(ApplicationService service) {
        System.out.println(">>> ApplicationService injected = " + (service != null));
        this.applicationService = service;
    }

    // Optional: Also add setter for job if ControllerFactory will inject it
    public void setJob(JobPosting job) {
        this.job = job;
    }

    // ---------- INITIALIZE (SIMPLIFIED) ----------
    @FXML
    public void initialize() {
        btnUpload.setOnAction(this::openUploadPage);
        btnSubmit.setOnAction(this::submitApplication);
        btnBack.setOnAction(this::goBack);

        // No need to check if applicationService is null here
        // It's guaranteed to be injected by the time initialize() is called
    }

    // ---------- DOCUMENT UPLOAD FROM CHILD SCREEN (NO CHANGE) ----------
    public void setUploadedFiles(List<String> files) {
        uploadedFiles.clear();

        if (files != null) {
            uploadedFiles.addAll(files);
            filesList.getItems().setAll(files);

            // Auto-fill from CV
            if (!files.isEmpty()) {
                try {
                    File file = new File(files.get(0));
                    String text = CVParser.extractText(file);

                    if (text != null && !text.trim().isEmpty()) {
                        autoFillFromCV(text);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    messageLabel.setText("Warning: unable to parse uploaded CV.");
                }
            }
        }
    }

    // ---------- OPEN UPLOAD PAGE (SIMPLIFIED) ----------
    private void openUploadPage(ActionEvent event) {
        try {
            // Use ControllerFactory for the UploadDocumentsController
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/upload_documents.fxml"));
            loader.setControllerFactory(com.recruitment.app.di.ControllerFactory.getInstance());

            Parent root = loader.load();
            UploadDocumentsController controller = loader.getController();

            // Set job and previous files
            controller.setJob(job);
            controller.setPreviousUploadedFiles(uploadedFiles);

            Stage modal = new Stage();
            modal.setTitle("Upload Files");
            modal.setScene(new Scene(root));
            modal.initOwner(((Node) event.getSource()).getScene().getWindow());
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.showAndWait();

            // After dialog closes → refresh updated list
            setUploadedFiles(controller.getSelectedFiles());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- SUBMIT APPLICATION (SIMPLIFIED) ----------
    private void submitApplication(ActionEvent event) {
        try {
            // REMOVED: Null check for applicationService (it's guaranteed)
            // if (applicationService == null) { ... }

            Application app = new Application();
            app.setUserId(SessionManager.loggedInUser.getId());
            app.setJobId(job.getId());
            app.setQualification(qualificationField.getText());
            app.setExperience(experienceField.getText());
            app.setCoverLetter(coverLetterField.getText());
            app.setStatus("submitted");

            applicationService.submit(app);

            messageLabel.setText("Application Submitted Successfully!");

            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error while submitting application.");
        }
    }

    private void clearForm() {
        qualificationField.clear();
        experienceField.clear();
        coverLetterField.clear();
        filesList.getItems().clear();
        uploadedFiles.clear();
    }

    // ---------- BACK (NO CHANGE) ----------
    private void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/job_details.fxml");
    }

    // ---------- CV PARSING METHODS (NO CHANGE) ----------
    private String extractEmail(String text) {
        Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+")
                .matcher(text);
        return m.find() ? m.group() : null;
    }

    private String extractPhone(String text) {
        Matcher m = Pattern.compile("(?:\\+?\\d{1,3}[\\s-]?)?(?:\\(?\\d{2,4}\\)?[\\s-]?)?\\d{3,4}[\\s-]?\\d{3,4}")
                .matcher(text);

        while (m.find()) {
            String digits = m.group().replaceAll("\\D", "");
            if (digits.length() >= 7 && digits.length() <= 15) {
                return m.group().trim();
            }
        }
        return null;
    }

    private String extractName(String text) {
        for (String line : text.split("\\r?\\n")) {
            String t = line.trim();
            if (t.isEmpty()) continue;

            String up = t.toUpperCase();
            if (up.contains("EDUCATION") || up.contains("EXPERIENCE") || up.contains("PROFILE"))
                continue;

            if (t.matches("^[A-Z][A-Za-z .'-]{1,60}$")
                    || (t.equals(t.toUpperCase()) && t.length() < 60))
                return t;

            if (t.length() < 40 && t.contains(" "))
                return t;
        }
        return null;
    }

    private void autoFillFromCV(String text) {
        if (text == null || text.trim().isEmpty()) return;

        String clean = text.replace("\r", "\n")
                .replaceAll("\\n{2,}", "\n")
                .trim();

        // ---------- BASIC: Email, Phone, Name ----------
        String email = extractEmail(clean);
        if (email != null && emailField.getText().isBlank())
            emailField.setText(email);

        String phone = extractPhone(clean);
        if (phone != null && contactField.getText().isBlank())
            contactField.setText(phone);

        String name = extractName(clean);
        if (name != null && fullNameField.getText().isBlank())
            fullNameField.setText(name);

        // ---------- IMPROVED SECTIONS ----------
        Map<String, String> sections = extractSections(clean);

        // EDUCATION
        if (sections.get("education") != null) {
            qualificationField.setText(sections.get("education").trim());
        }

        // SKILLS
        if (sections.get("skills") != null) {
            qualificationField.appendText("\n\nSkills:\n" +
                    sections.get("skills").trim());
        }

        // EXPERIENCE
        if (sections.get("experience") != null) {
            experienceField.setText(sections.get("experience").trim());
        }

        // PROJECTS
        if (sections.get("projects") != null) {
            experienceField.appendText("\n\nProjects:\n" +
                    sections.get("projects").trim());
        }
    }

    private Map<String, String> extractSections(String text) {
        Map<Integer, String> indexToHeader = new TreeMap<>();
        Map<String, String> result = new HashMap<>();

        // All heading patterns you want to detect
        Map<String, String> headerMap = Map.ofEntries(
                Map.entry("EDUCATION", "education"),
                Map.entry("ACADEMIC", "education"),
                Map.entry("QUALIFICATION", "education"),

                Map.entry("EXPERIENCE", "experience"),
                Map.entry("WORK EXPERIENCE", "experience"),
                Map.entry("EMPLOYMENT", "experience"),

                Map.entry("SKILLS", "skills"),
                Map.entry("TECHNICAL SKILLS", "skills"),
                Map.entry("SKILL SET", "skills"),

                Map.entry("PROJECTS", "projects"),
                Map.entry("PERSONAL PROJECTS", "projects")
        );

        String upper = text.toUpperCase();

        for (String key : headerMap.keySet()) {
            int pos = upper.indexOf(key);
            if (pos >= 0) {
                indexToHeader.put(pos, headerMap.get(key));
            }
        }

        // If nothing found → return empty
        if (indexToHeader.isEmpty()) return result;

        List<Integer> positions = new ArrayList<>(indexToHeader.keySet());

        for (int i = 0; i < positions.size(); i++) {
            int start = positions.get(i);
            int end = (i == positions.size() - 1)
                    ? text.length()
                    : positions.get(i + 1);

            String sectionType = indexToHeader.get(start);

            // Remove the header itself
            String body = text.substring(start, end)
                    .replaceFirst("(?i)" + sectionType, "")
                    .trim();

            result.put(sectionType, body);
        }

        return result;
    }
}