package com.recruitment.app.controllers;

import com.recruitment.app.models.FinalRankedCandidate;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.FinalRankingService;
import com.recruitment.app.services.HMService;
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

    private final FinalRankingService finalRankingService; // interface only
    private final HMService hmService;                     // interface only

    // Constructor injection
    public FinalRankingController(FinalRankingService finalRankingService, HMService hmService) {
        this.finalRankingService = finalRankingService;
        this.hmService = hmService;
    }

    @FXML
    private void initialize() {
        // Table column setup
        colRank.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getRank()));
        colApplicantName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getApplicantName()));
        colScore.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getCompositeScore()));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));

        sendToHMButton.setDisable(true);

        populateJobs();
    }

    private void populateJobs() {
        if (jobComboBox == null) return;

        try {
            List<JobPosting> jobs = finalRankingService.getJobsEligibleForFinalRanking();
            jobComboBox.setItems(FXCollections.observableArrayList(jobs));
            if (!jobs.isEmpty()) jobComboBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
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
                generateFinalRanking();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to send candidates to Hiring Manager.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred while sending candidates to HM.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}
