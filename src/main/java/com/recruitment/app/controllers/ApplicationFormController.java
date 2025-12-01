package com.recruitment.app.controllers;

import com.recruitment.app.dao.ApplicationDAO;
import com.recruitment.app.dao.ApplicationDAOImpl;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.utils.CVParser;
import com.recruitment.app.utils.SceneLoader;
import com.recruitment.app.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import com.recruitment.app.config.DBConnection;

import javafx.event.ActionEvent;
import com.recruitment.app.models.Application;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationFormController {

    private JobPosting job;
    private List<String> uploadedFiles = new ArrayList<>();

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
            filesList.getItems().setAll(files); // show in list

            // Auto-extract text from first uploaded CV and autofill
            if (!files.isEmpty()) {
                try {
                    File file = new File(files.get(0)); // first CV
                    String cvText = CVParser.extractText(file);
                    if (cvText != null && !cvText.trim().isEmpty()) {
                        autoFillFromCV(cvText);
                    }
                } catch (Exception e) {
                    // don't crash UI; just show a message for debugging
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
            up.setPreviousUploadedFiles(uploadedFiles); // Pass already selected files
        });
    }

    public void submitApplication(ActionEvent event) {
        try {
            Application application = new Application();

            application.setUserId(SessionManager.loggedInUser.getId());
            application.setJobId(job.getId());
            application.setQualification(qualificationField.getText());
            application.setExperience(experienceField.getText());
            application.setCoverLetter(coverLetterField.getText());
            application.setStatus("submitted");

            ApplicationDAO dao = new ApplicationDAOImpl(DBConnection.getConnection());
            dao.save(application);

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
    // Autofill helpers
    // ----------------------

    /**
     * Tries to auto-fill the form from extracted CV text.
     * This is a heuristic approach — it looks for common headings and patterns.
     */
    private void autoFillFromCV(String text) {
        if (text == null || text.trim().isEmpty()) return;

        // normalize spacing
        String normalized = text.replace("\r", "\n").replaceAll("\\n{2,}", "\n\n").trim();

        // 1) Extract email
        String email = extractEmail(normalized);
        if (email != null && emailField.getText().isEmpty()) {
            emailField.setText(email);
        }

        // 2) Extract phone
        String phone = extractPhone(normalized);
        if (phone != null && contactField.getText().isEmpty()) {
            contactField.setText(phone);
        }

        // 3) Name heuristic: first non-empty line that looks like a name (all caps or title-case, not "EDUCATION")
        String name = extractName(normalized);
        if (name != null && fullNameField.getText().isEmpty()) {
            fullNameField.setText(name);
        }

        // 4) Extract sections by header keywords (case-insensitive)
        // We'll search for "SKILL", "TECHNICAL SKILL", "PROJECT", "EXPERIENCE", "WORK EXPERIENCE", "INTERNSHIP", "EDUCATION"
        Map<String, String> sections = extractSections(normalized);

        // Skills / qualifications
        String skills = firstNonEmpty(sections.get("skills"), sections.get("technical_skills"), sections.get("abilities"));
        if (skills != null && !skills.isBlank()) {
            // append to qualifications field
            qualificationField.setText(skills.trim());
        }

        // Projects
        String projects = sections.get("projects");
        if (projects != null && !projects.isBlank()) {
            // we don't have a dedicated projects field; append to experienceField or qualificationField
            // We'll append to experienceField (projects often show hands-on experience)
            String existingExp = experienceField.getText();
            String combinedExp = (existingExp == null ? "" : existingExp + "\n") + projects.trim();
            experienceField.setText(combinedExp.trim());
        }

        // Experience / internships / work
        String experience = firstNonEmpty(sections.get("experience"), sections.get("work_experience"), sections.get("internship"));
        if (experience != null && !experience.isBlank()) {
            String existingExp = experienceField.getText();
            String combinedExp = (existingExp == null || existingExp.isBlank()) ? experience.trim()
                    : existingExp + "\n" + experience.trim();
            experienceField.setText(combinedExp.trim());
        }

        // Education: sometimes we want to put into qualification field
        String education = sections.get("education");
        if (education != null && !education.isBlank()) {
            String existingQual = qualificationField.getText();
            String combinedQual = (existingQual == null || existingQual.isBlank()) ? education.trim()
                    : existingQual + "\n" + education.trim();
            qualificationField.setText(combinedQual.trim());
        }

        // Optionally put the full extracted CV text into coverLetterField preview (or leave it)
        // coverLetterField.setText("Extracted from CV:\n" + normalized.substring(0, Math.min(3000, normalized.length())));
    }

    private String firstNonEmpty(String... candidates) {
        if (candidates == null) return null;
        for (String c : candidates) {
            if (c != null && !c.isBlank()) return c;
        }
        return null;
    }

    private String extractEmail(String text) {
        Pattern p = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
        Matcher m = p.matcher(text);
        if (m.find()) return m.group();
        return null;
    }

    private String extractPhone(String text) {
        // Basic international/local phone number patterns (very permissive)
        Pattern p = Pattern.compile("(?:\\+?\\d{1,3}[\\s-]?)?(?:\\(?\\d{2,4}\\)?[\\s-]?)?\\d{3,4}[\\s-]?\\d{3,4}");
        Matcher m = p.matcher(text);
        List<String> matches = new ArrayList<>();
        while (m.find()) {
            String candidate = m.group().trim();
            // simple filter: must contain at least 7 digits
            String digitsOnly = candidate.replaceAll("\\D", "");
            if (digitsOnly.length() >= 7 && digitsOnly.length() <= 15) matches.add(candidate);
        }
        if (!matches.isEmpty()) return matches.get(0);
        return null;
    }

    private String extractName(String text) {
        // Heuristic: first lines until first header (EDUCATION/PROFILE/CONTACT/EXPERIENCE)
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            String up = t.toUpperCase();
            if (up.contains("EDUCATION") || up.contains("PROFILE") || up.contains("CONTACT") || up.contains("EXPERIENCE") || up.contains("SKILL"))
                continue;
            // if line looks like a name (letters, spaces, possibly uppercase)
            if (t.matches("^[A-Z][A-Za-z .'-]{1,60}$") || t.equals(t.toUpperCase()) && t.length() < 60) {
                return t;
            }
            // fallback: if first non-empty line shorter than 40 and contains a space, treat as name
            if (t.length() < 40 && t.contains(" ")) return t;
        }
        return null;
    }

    /**
     * Find common section blocks by header keywords. Returns a map with keys:
     * technical_skills, skills, projects, experience, work_experience, internship, education, summary, profile
     */
    private Map<String, String> extractSections(String text) {
        Map<String, String> result = new HashMap<>();
        // We'll find header positions (case-insensitive)
        String upper = text.toUpperCase();

        // Define headers and canonical keys
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

        // Find indices of headers in text
        List<Integer> positions = new ArrayList<>();
        List<String> keysAtPos = new ArrayList<>();
        for (Map.Entry<String, String> e : headers.entrySet()) {
            String header = e.getKey();
            int idx = upper.indexOf(header);
            if (idx >= 0) {
                positions.add(idx);
                keysAtPos.add(e.getValue());
            }
        }

        // If no headers found, fallback: try to extract "SKILLS:" inline
        if (positions.isEmpty()) {
            // quick inline: find "SKILLS" followed by colon in original text
            Pattern pInline = Pattern.compile("(?i)SKILL[S]?:\\s*(.+)");
            Matcher mInline = pInline.matcher(text);
            if (mInline.find()) result.put("skills", mInline.group(1).trim());
            return result;
        }

        // Sort positions
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) order.add(i);
        order.sort(Comparator.comparingInt(positions::get));

        for (int i = 0; i < order.size(); i++) {
            int idx = positions.get(order.get(i));
            String key = keysAtPos.get(order.get(i));
            int start = idx;
            int end = text.length();
            if (i + 1 < order.size()) end = positions.get(order.get(i + 1));
            String block = text.substring(start, end).trim();
            // remove header line itself
            String[] blockLines = block.split("\\r?\\n");
            if (blockLines.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (int j = 1; j < blockLines.length; j++) { // skip header line
                    sb.append(blockLines[j]).append("\n");
                }
                result.put(key, sb.toString().trim());
            } else {
                result.put(key, "");
            }
        }

        return result;
    }
}
