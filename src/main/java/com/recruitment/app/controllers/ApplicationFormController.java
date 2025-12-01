package com.recruitment.app.controllers;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.Application;
import com.recruitment.app.services.ApplicationService;
import com.recruitment.app.utils.CVParser;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    private ApplicationService applicationService;   // <-- SERVICE INJECTION

    // -------- SERVICE SETTER --------
    public void setApplicationService(ApplicationService service) {
        this.applicationService = service;
    }

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField contactField;
    @FXML private TextArea qualificationField;
    @FXML private TextArea experienceField;
    @FXML private TextArea coverLetterField;
    @FXML private ListView<String> filesList;
    @FXML private Label messageLabel;

    public void setJob(JobPosting job) {
        this.job = job;
    }

    // called by UploadDocumentsController after returning
    public void setUploadedFiles(List<String> files) {
        uploadedFiles.clear();
        if (files != null) {
            uploadedFiles.addAll(files);
            filesList.getItems().setAll(files);

            if (!files.isEmpty()) {
                try {
                    File file = new File(files.get(0));
                    String cvText = CVParser.extractText(file);
                    if (cvText != null && !cvText.trim().isEmpty()) {
                        autoFillFromCV(cvText);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    messageLabel.setText("Warning: unable to parse uploaded CV.");
                }
            }
        }
    }

    @FXML
    public void openUploadPage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        SceneLoader.loadWithData(stage, "/ui/upload_documents.fxml", controller -> {
            UploadDocumentsController up = (UploadDocumentsController) controller;
            up.setJob(job);
            up.setPreviousUploadedFiles(uploadedFiles);
        });
    }

    // FINAL VERSION using SERVICE
    @FXML
    public void submitApplication(ActionEvent event) {
        try {
            if (applicationService == null) {
                messageLabel.setText("ERROR: ApplicationService not injected!");
                return;
            }

            Application application = new Application();
            application.setUserId(SessionManager.loggedInUser.getId());
            application.setJobId(job.getId());
            application.setQualification(qualificationField.getText());
            application.setExperience(experienceField.getText());
            application.setCoverLetter(coverLetterField.getText());
            application.setStatus("submitted");

            applicationService.submit(application);

            messageLabel.setText("Application Submitted Successfully!");
        }
        catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error while submitting application.");
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneLoader.load(stage, "/ui/job_details.fxml");
    }

    // ----------------------
    // Autofill helpers (unchanged)
    // ----------------------

    private void autoFillFromCV(String text) {
        if (text == null || text.trim().isEmpty()) return;

        String normalized = text.replace("\r", "\n").replaceAll("\\n{2,}", "\n\n").trim();

        String email = extractEmail(normalized);
        if (email != null && emailField.getText().isEmpty()) emailField.setText(email);

        String phone = extractPhone(normalized);
        if (phone != null && contactField.getText().isEmpty()) contactField.setText(phone);

        String name = extractName(normalized);
        if (name != null && fullNameField.getText().isEmpty()) fullNameField.setText(name);

        Map<String, String> sections = extractSections(normalized);

        String skills = firstNonEmpty(sections.get("skills"), sections.get("technical_skills"), sections.get("abilities"));
        if (skills != null) qualificationField.setText(skills.trim());

        String projects = sections.get("projects");
        if (projects != null) {
            experienceField.setText(experienceField.getText() + "\n" + projects.trim());
        }

        String experience = firstNonEmpty(sections.get("experience"), sections.get("work_experience"), sections.get("internship"));
        if (experience != null) {
            experienceField.setText(experienceField.getText() + "\n" + experience.trim());
        }

        String education = sections.get("education");
        if (education != null) {
            qualificationField.setText(qualificationField.getText() + "\n" + education.trim());
        }
    }

    private String firstNonEmpty(String... candidates) {
        if (candidates == null) return null;
        for (String c : candidates) if (c != null && !c.isBlank()) return c;
        return null;
    }

    private String extractEmail(String text) {
        Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(text);
        return m.find() ? m.group() : null;
    }

    private String extractPhone(String text) {
        Matcher m = Pattern.compile("(?:\\+?\\d{1,3}[\\s-]?)?(?:\\(?\\d{2,4}\\)?[\\s-]?)?\\d{3,4}[\\s-]?\\d{3,4}").matcher(text);
        while (m.find()) {
            String num = m.group().trim().replaceAll("\\D", "");
            if (num.length() >= 7 && num.length() <= 15) return m.group().trim();
        }
        return null;
    }

    private String extractName(String text) {
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            String up = t.toUpperCase();
            if (up.contains("EDUCATION") || up.contains("PROFILE") || up.contains("CONTACT") ||
                    up.contains("EXPERIENCE") || up.contains("SKILL")) continue;

            if (t.matches("^[A-Z][A-Za-z .'-]{1,60}$") || (t.equals(t.toUpperCase()) && t.length() < 60))
                return t;

            if (t.length() < 40 && t.contains(" ")) return t;
        }
        return null;
    }

    private Map<String, String> extractSections(String text) {
        Map<String, String> result = new HashMap<>();
        String upper = text.toUpperCase();

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("TECHNICAL SKILLS", "technical_skills");
        headers.put("TECHNICAL SKILL", "technical_skills");
        headers.put("SKILLS", "skills");
        headers.put("PROJECTS", "projects");
        headers.put("EXPERIENCE", "experience");
        headers.put("WORK EXPERIENCE", "work_experience");
        headers.put("INTERNSHIP", "internship");
        headers.put("EDUCATION", "education");
        headers.put("PROFILE", "profile");
        headers.put("SUMMARY", "summary");
        headers.put("ACHIEVEMENTS", "achievements");

        List<Integer> pos = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        for (var e : headers.entrySet()) {
            int idx = upper.indexOf(e.getKey());
            if (idx >= 0) {
                pos.add(idx);
                keys.add(e.getValue());
            }
        }

        if (pos.isEmpty()) {
            Matcher m = Pattern.compile("(?i)SKILL[S]?:\\s*(.+)").matcher(text);
            if (m.find()) result.put("skills", m.group(1).trim());
            return result;
        }

        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < pos.size(); i++) order.add(i);
        order.sort(Comparator.comparingInt(pos::get));

        for (int i = 0; i < order.size(); i++) {
            int idx = pos.get(order.get(i));
            String key = keys.get(order.get(i));
            int end = (i + 1 < order.size()) ? pos.get(order.get(i + 1)) : text.length();

            String block = text.substring(idx, end).trim();
            String[] linesArr = block.split("\\r?\\n");

            if (linesArr.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (int j = 1; j < linesArr.length; j++) sb.append(linesArr[j]).append("\n");
                result.put(key, sb.toString().trim());
            }
        }

        return result;
    }
}
