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

    // --- Service injection setters ---
    public void setHMService(HMService hmService) { this.hmService = hmService; }
    public void setRecruiterService(RecruiterService rs) { this.recruiterService = rs; }
    public void setUserService(UserService us) { this.userService = us; }
    public void setNoteService(NoteService ns) { this.noteService = ns; }
    public void setCurrentUserId(int id) { this.currentUserId = id; }

    @FXML
    private void initialize() {
        candidateDetailsPanel.setDisable(true);

        colRank.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getRank()));
        colApplicantName.setCellValueFactory(data -> new ReadOnlyStringWrapper(getApplicantName(data.getValue())));
        colScore.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getCompositeScore()));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));
        colDecision.setCellValueFactory(data -> new ReadOnlyStringWrapper(getApplicationDecision(data.getValue())));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("View Details");
            { btn.setOnAction(e -> viewApplicantDetails(getTableView().getItems().get(getIndex()))); }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        noteTypeComboBox.setItems(FXCollections.observableArrayList(ApplicantNote.NoteType.values()));
        noteTypeComboBox.getSelectionModel().selectFirst();

        candidateTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> onCandidateSelected(newSel)
        );

        disableCandidateActions(true);

        notesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ApplicantNote note, boolean empty) {
                super.updateItem(note, empty);
                setText((empty || note == null) ? null : note.getNoteText());
            }
        });

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
        candidateTable.setItems(FXCollections.observableArrayList(candidates));
        candidateDetailsPanel.setDisable(true);
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

    private void viewApplicantDetails(FinalRankedCandidate candidate) {
        if (candidate == null) return;
        Application app = recruiterService.getApplicationById(candidate.getApplicationId());
        if (app == null) { showAlert(Alert.AlertType.ERROR, "Failed to load application."); return; }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Applicant Details");
        dialog.setHeaderText("Application Details for " + getApplicantName(candidate));

        VBox box = new VBox(10);
        box.getChildren().addAll(
                new Label("Applicant: " + getApplicantName(candidate)),
                new Label("Qualifications: " + app.getQualification()),
                new Label("Experience: " + app.getExperience()),
                new Label("Cover Letter: " + (app.getCoverLetter() != null ? app.getCoverLetter() : "N/A")),
                new Label("Applied: " + app.getAppliedAt()),
                new Label("Current Status: " + app.getStatus())
        );

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    @FXML
    private void addNote() {
        FinalRankedCandidate c = candidateTable.getSelectionModel().getSelectedItem();
        if (c == null) { showAlert(Alert.AlertType.WARNING, "Select a candidate first."); return; }

        String text = noteTextArea.getText().trim();
        if (text.isEmpty()) { showAlert(Alert.AlertType.WARNING, "Enter note text."); return; }

        try {
            if (currentlyEditingNote != null) {
                currentlyEditingNote.setNoteText(text);
                currentlyEditingNote.setNoteType(noteTypeComboBox.getValue());
                noteService.updateNote(currentlyEditingNote);
                showAlert(Alert.AlertType.INFORMATION, "Note updated!");
            } else {
                ApplicantNote note = new ApplicantNote((long)c.getApplicationId(), (long)currentUserId, text, noteTypeComboBox.getValue());
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
    private void selectCandidate() { makeHiringDecision("SELECTED"); }
    @FXML
    private void rejectCandidate() { makeHiringDecision("REJECTED"); }

    private void makeHiringDecision(String decision) {
        FinalRankedCandidate c = candidateTable.getSelectionModel().getSelectedItem();
        if (c == null) { showAlert(Alert.AlertType.WARNING, "Select a candidate first."); return; }

        try {
            boolean ok = hmService.updateHiringDecision(c.getApplicationId(), decision);
            if (!ok) { showAlert(Alert.AlertType.ERROR, "Failed to update decision."); return; }

            hmService.updateCandidateStatus(c.getId(), "HM_REVIEWED");
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
}
