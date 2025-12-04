package com.recruitment.app.controllers;

import com.recruitment.app.models.*;
import com.recruitment.app.services.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class HMCandidateReviewController {

    @FXML private ComboBox<JobPosting> jobDropdown;
    @FXML private Label statusLabel;

    @FXML private TableView<FinalRankedCandidate> candidateTable;
    @FXML private TableColumn<FinalRankedCandidate, Integer> colRank;
    @FXML private TableColumn<FinalRankedCandidate, String> colApplicantName;
    @FXML private TableColumn<FinalRankedCandidate, Double> colScore;
    @FXML private TableColumn<FinalRankedCandidate, String> colStatus;
    @FXML private TableColumn<FinalRankedCandidate, String> colDecision;
    @FXML private TableColumn<FinalRankedCandidate, Void> colActions;

    @FXML private VBox candidateDetailsPanel;
    @FXML private Button selectButton;
    @FXML private Button rejectButton;
    @FXML private Button viewDetailsButton;
    @FXML private TextArea noteTextArea;
    @FXML private ComboBox<ApplicantNote.NoteType> noteTypeComboBox;
    @FXML private Button addNoteButton;
    @FXML private ListView<ApplicantNote> notesListView;

    // --- Injected services ---
    private HMService hmService;
    private RecruiterService recruiterService;
    private UserService userService;
    private NoteService noteService;

    // --- Controller state ---
    private int currentUserId;
    private JobPosting currentJob;
    private ApplicantNote currentlyEditingNote = null;
    private NotificationService notificationService;

    // ---------- DEFAULT CONSTRUCTOR ----------
    public HMCandidateReviewController() {
        // Empty - services will be injected
    }

    // --- Service injection setters (CONSOLIDATED) ---
    public void setServices(
            HMService hmService,
            RecruiterService recruiterService,
            UserService userService,
            NoteService noteService,
            NotificationService notificationService
    ) {
        this.hmService = hmService;
        this.recruiterService = recruiterService;
        this.userService = userService;
        this.noteService = noteService;
        this.notificationService = notificationService;
    }


    // --- Runtime data setter (KEEP) ---
    public void setCurrentUserId(int id) {
        this.currentUserId = id;
    }

    @FXML
    private void initialize() {
        candidateDetailsPanel.setDisable(true);

        // Setup table columns
        colRank.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getRank()));
        colApplicantName.setCellValueFactory(data -> new ReadOnlyStringWrapper(getApplicantName(data.getValue())));
        colScore.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getCompositeScore()));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));
        colDecision.setCellValueFactory(data -> new ReadOnlyStringWrapper(getApplicationDecision(data.getValue())));

        // Remove the button from table column since we want only one button in details panel
        colActions.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null); // No button in table
            }
        });

        // Setup note type combo box
        noteTypeComboBox.setItems(FXCollections.observableArrayList(ApplicantNote.NoteType.values()));
        noteTypeComboBox.getSelectionModel().selectFirst();

        // When a candidate is selected, enable details panel
        candidateTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> onCandidateSelected(newSel)
        );

        // Initially disable all actions
        disableCandidateActions(true);

        // Setup notes list view
        notesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ApplicantNote note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null) {
                    setText(null);
                } else {
                    setText(String.format("[%s] %s", note.getNoteType(), note.getNoteText()));
                }
            }
        });

        // When a note is selected, load it for editing
        notesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldNote, newNote) -> {
            if (newNote != null) {
                noteTextArea.setText(newNote.getNoteText());
                noteTypeComboBox.setValue(newNote.getNoteType());
                currentlyEditingNote = newNote;
                addNoteButton.setText("Update Note");
            } else {
                noteTextArea.clear();
                noteTypeComboBox.getSelectionModel().selectFirst();
                currentlyEditingNote = null;
                addNoteButton.setText("Add Note");
            }
        });
    }

    // --- Must be called AFTER service injection ---
    public void loadInitialData() {
        loadJobsForHM();
    }

    private void loadJobsForHM() {
        if (hmService == null) {
            statusLabel.setText("Service not initialized!");
            return;
        }

        List<JobPosting> jobs = hmService.getJobsForHM(currentUserId);

        if (jobs != null && !jobs.isEmpty()) {
            jobDropdown.setItems(FXCollections.observableArrayList(jobs));
            statusLabel.setText("Select a job to review candidates.");
        } else {
            statusLabel.setText("No jobs available for your company.");
        }
    }

    @FXML
    private void loadCandidatesForSelectedJob() {
        currentJob = jobDropdown.getSelectionModel().getSelectedItem();
        if (currentJob == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a job first.");
            return;
        }

        boolean sent = hmService.hasRecruiterSubmittedFinalList(currentJob.getId());
        if (!sent) {
            showAlert(Alert.AlertType.INFORMATION, "Recruiter has not sent the final list for this job.");
            candidateTable.getItems().clear();
            candidateDetailsPanel.setDisable(true);
            statusLabel.setText("Waiting for recruiter to send final list...");
            return;
        }

        List<FinalRankedCandidate> candidates = hmService.getCandidatesSentToHM(currentJob.getId());

        // Check if candidates list is null or empty
        if (candidates == null || candidates.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No candidates sent for review for this job.");
            candidateTable.getItems().clear();
            candidateDetailsPanel.setDisable(true);
            statusLabel.setText("No candidates available for review.");
            return;
        }

        candidateTable.setItems(FXCollections.observableArrayList(candidates));
        candidateDetailsPanel.setDisable(true); // Will be enabled when a candidate is selected
        statusLabel.setText("Candidates loaded. Select a candidate to review.");
    }

    private void onCandidateSelected(FinalRankedCandidate c) {
        if (c == null) {
            disableCandidateActions(true);
            clearCandidateDetails();
            return;
        }

        candidateDetailsPanel.setDisable(false);
        disableCandidateActions(false);
        loadCandidateNotes(c);

        Application app = recruiterService.getApplicationById(c.getApplicationId());
        if (app != null) {
            selectButton.setDisable("SELECTED".equals(app.getStatus()));
            rejectButton.setDisable("REJECTED".equals(app.getStatus()));
        }
    }

    private void loadCandidateNotes(FinalRankedCandidate c) {
        if (noteService != null) {
            List<ApplicantNote> notes = noteService.getNotesByApplication((long)c.getApplicationId());
            notesListView.setItems(FXCollections.observableArrayList(notes));
        }
    }

    private void clearCandidateDetails() {
        notesListView.getItems().clear();
        noteTextArea.clear();
    }

    private void disableCandidateActions(boolean disable) {
        selectButton.setDisable(disable);
        rejectButton.setDisable(disable);
        addNoteButton.setDisable(disable);
        // View details button should be disabled when no candidate is selected
        viewDetailsButton.setDisable(disable);
        noteTextArea.setDisable(disable);
        noteTypeComboBox.setDisable(disable);
    }

    private String getApplicantName(FinalRankedCandidate c) {
        Application app = recruiterService.getApplicationById(c.getApplicationId());
        if (app == null) return "Unknown Applicant";
        User user = userService.getUserById(app.getUserId());
        return user != null ? user.getFullName() : "Unknown Applicant";
    }

    private String getApplicationDecision(FinalRankedCandidate c) {
        Application app = recruiterService.getApplicationById(c.getApplicationId());
        return (app != null && app.getStatus() != null) ? app.getStatus() : "PENDING";
    }

    @FXML
    private void viewApplicantDetails() {
        FinalRankedCandidate candidate = candidateTable.getSelectionModel().getSelectedItem();
        if (candidate == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a candidate first.");
            return;
        }

        // Get the full application details
        Application app = recruiterService.getApplicationById(candidate.getApplicationId());
        if (app == null) {
            showAlert(Alert.AlertType.ERROR, "Failed to load application details.");
            return;
        }

        // Get user details
        User user = userService.getUserById(app.getUserId());
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Failed to load applicant information.");
            return;
        }

        // Create a detailed dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Applicant Details");
        dialog.setHeaderText("Complete Application Details");

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(10));

        // Personal Information
        Label personalHeader = new Label("Personal Information");
        personalHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label nameLabel = new Label("Full Name: " + user.getFullName());
        Label emailLabel = new Label("Email: " + user.getEmail());
        Label phoneLabel = new Label("Phone: " + (user.getContact() != null ? user.getContact() : "Not provided"));

        // Application Details
        Label appHeader = new Label("Application Details");
        appHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label jobLabel = new Label("Job Applied: " + (currentJob != null ? currentJob.getTitle() : "Unknown"));
        Label qualificationLabel = new Label("Qualifications: " + app.getQualification());
        Label experienceLabel = new Label("Experience: " + app.getExperience());
        Label coverLetterLabel = new Label("Cover Letter: " +
                (app.getCoverLetter() != null && !app.getCoverLetter().isEmpty() ?
                        app.getCoverLetter() : "Not provided"));
        Label appliedDateLabel = new Label("Applied Date: " + app.getAppliedAt());
        Label statusLabel = new Label("Current Status: " + app.getStatus());

        // Score Information
        Label scoreHeader = new Label("Assessment Scores");
        scoreHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label rankLabel = new Label("Final Rank: " + candidate.getRank());
        Label scoreLabel = new Label("Composite Score: " + candidate.getCompositeScore());
        Label tableStatusLabel = new Label("Ranking Status: " + candidate.getStatus());

        // Add all labels to content
        content.getChildren().addAll(
                personalHeader, nameLabel, emailLabel, phoneLabel,
                new Separator(),
                appHeader, jobLabel, qualificationLabel, experienceLabel,
                coverLetterLabel, appliedDateLabel, statusLabel,
                new Separator(),
                scoreHeader, rankLabel, scoreLabel, tableStatusLabel
        );

        // Add a scroll pane in case content is long
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().setPrefSize(500, 450);

        dialog.showAndWait();
    }

    @FXML
    private void addNote() {
        FinalRankedCandidate c = candidateTable.getSelectionModel().getSelectedItem();
        if (c == null) {
            showAlert(Alert.AlertType.WARNING, "Select a candidate first.");
            return;
        }

        String text = noteTextArea.getText().trim();
        if (text.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Enter note text.");
            return;
        }

        try {
            if (currentlyEditingNote != null) {
                currentlyEditingNote.setNoteText(text);
                currentlyEditingNote.setNoteType(noteTypeComboBox.getValue());
                noteService.updateNote(currentlyEditingNote);
                showAlert(Alert.AlertType.INFORMATION, "Note updated!");
            } else {
                ApplicantNote note = new ApplicantNote(
                        (long)c.getApplicationId(),
                        (long)currentUserId,
                        text,
                        noteTypeComboBox.getValue()
                );
                noteService.createNote(note);
                showAlert(Alert.AlertType.INFORMATION, "Note added!");
            }

            noteTextArea.clear();
            noteTypeComboBox.getSelectionModel().selectFirst();
            currentlyEditingNote = null;
            addNoteButton.setText("Add Note");
            loadCandidateNotes(c);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to save note:\n" + e.getMessage());
        }
    }

    @FXML
    private void selectCandidate() {
        makeHiringDecision("SELECTED");
    }

    @FXML
    private void rejectCandidate() {
        makeHiringDecision("REJECTED");
    }
    private void makeHiringDecision(String decision) {
        FinalRankedCandidate c = candidateTable.getSelectionModel().getSelectedItem();
        if (c == null) {
            showAlert(Alert.AlertType.WARNING, "Select a candidate first.");
            return;
        }

        try {
            boolean ok = hmService.updateHiringDecision(c.getApplicationId(), decision);
            if (!ok) {
                showAlert(Alert.AlertType.ERROR, "Failed to update decision.");
                return;
            }

            hmService.updateCandidateStatus(c.getId(), "HM_REVIEWED");

            // --- SEND NOTIFICATION TO APPLICANT ---
            Application app = recruiterService.getApplicationById(c.getApplicationId());
            if (app != null) {

                String subject = "Update on your Job Application";
                String message;

                if (decision.equals("SELECTED")) {
                    message = "Congratulations! You have been SELECTED for the position: "
                            + currentJob.getTitle();
                } else {
                    message = "Thank you for applying. Unfortunately, you were NOT selected for: "
                            + currentJob.getTitle();
                }

                notificationService.sendNotificationToApplicant(app.getUserId(), subject, message);
            }

            showAlert(Alert.AlertType.INFORMATION, "Candidate " + decision + "!");
            loadCandidatesForSelectedJob();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.showAndWait();
    }
    @FXML
    private Button notifyButton;

    @FXML
    private void notifyCandidates() {

        if (currentJob == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a job first.");
            return;
        }

        boolean success = hmService.notifyCandidatesForJob(currentJob.getId());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION,
                    "All candidates have been notified successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR,
                    "Failed to notify candidates or no candidates were eligible.");
        }
    }


}
