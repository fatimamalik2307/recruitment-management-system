package com.recruitment.app.controllers;

import com.recruitment.app.models.Application;
import com.recruitment.app.models.FinalRankedCandidate;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.FinalRankingService;
import com.recruitment.app.services.HMService;
import com.recruitment.app.services.NotificationService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class FinalRankingController {

    @FXML private ComboBox<JobPosting> jobComboBox;
    @FXML private TableView<FinalRankedCandidate> finalRankingTable;
    @FXML private TableColumn<FinalRankedCandidate, Integer> colRank;
    @FXML private TableColumn<FinalRankedCandidate, String> colApplicantName;
    @FXML private TableColumn<FinalRankedCandidate, Double> colScore;
    @FXML private TableColumn<FinalRankedCandidate, String> colStatus;
    @FXML private Button sendToHMButton;
    @FXML private Button notifyCandidatesButton;

    private final FinalRankingService finalRankingService; // interface only
    private final HMService hmService;                     // interface only
    private final NotificationService notificationService; // interface only

    private int selectedJobId; // For notification purposes

    // Constructor injection ensures proper service injection
    public FinalRankingController(FinalRankingService finalRankingService,
                                  HMService hmService,
                                  NotificationService notificationService) {
        this.finalRankingService = finalRankingService;
        this.hmService = hmService;
        this.notificationService = notificationService;
    }

    @FXML
    private void initialize() {
        // Table column setup
        colRank.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getRank()));
        colApplicantName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getApplicantName()));
        colScore.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getCompositeScore()));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));

        sendToHMButton.setDisable(true);
        notifyCandidatesButton.setDisable(true);

        populateJobs();
    }

    private void populateJobs() {
        try {
            List<JobPosting> jobs = finalRankingService.getJobsEligibleForFinalRanking();
            jobComboBox.setItems(FXCollections.observableArrayList(jobs));
            if (!jobs.isEmpty()) jobComboBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to load jobs.");
        }
    }

    @FXML
    private void generateFinalRanking() {
        JobPosting job = jobComboBox.getValue();
        if (job == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a job.");
            return;
        }

        try {
            List<FinalRankedCandidate> finalList = finalRankingService.generateFinalRankingForJob(job.getId());
            finalRankingTable.setItems(FXCollections.observableArrayList(finalList));
            sendToHMButton.setDisable(finalList.isEmpty());
            notifyCandidatesButton.setDisable(finalList.isEmpty());
            selectedJobId = job.getId();

            showAlert(Alert.AlertType.INFORMATION, "Final ranking generated successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred while generating the final ranking.");
        }
    }

    @FXML
    private void sendToHiringManager() {
        JobPosting job = jobComboBox.getValue();
        if (job == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a job.");
            return;
        }

        try {
            boolean success = hmService.sendToHiringManager(job.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Candidates sent to Hiring Manager successfully!");
                sendToHMButton.setDisable(true);
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to send candidates to Hiring Manager.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred while sending candidates to HM.");
        }
    }

    @FXML
    private void notifyCandidates() {
        if (selectedJobId == 0) {
            showAlert(Alert.AlertType.WARNING, "Generate final ranking before notifying candidates.");
            return;
        }

        try {
            List<Application> finalRankings = finalRankingService.getFinalRankingApplications(selectedJobId);

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Notify ALL candidates of their final decision?",
                    ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                notificationService.notifyCandidates(finalRankings);
                showAlert(Alert.AlertType.INFORMATION, "All candidate notifications sent!");
                notifyCandidatesButton.setDisable(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred while notifying candidates.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}
