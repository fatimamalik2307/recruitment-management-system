package com.recruitment.app.controllers;

import com.recruitment.app.models.FinalRankedCandidate;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.FinalRankingServiceImpl;
import com.recruitment.app.services.HMService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class FinalRankingController {

    @FXML
    private ComboBox<JobPosting> jobComboBox;

    @FXML
    private TableView<FinalRankedCandidate> finalRankingTable;

    @FXML
    private TableColumn<FinalRankedCandidate, Integer> colRank;
    @FXML
    private TableColumn<FinalRankedCandidate, String> colApplicantName;
    @FXML
    private TableColumn<FinalRankedCandidate, Double> colScore;
    @FXML
    private TableColumn<FinalRankedCandidate, String> colStatus;

    @FXML
    private Button sendToHMButton;

    private FinalRankingServiceImpl finalRankingService;
    private HMService hmService; // Added HMService

    // No-arg constructor required by FXMLLoader
    public FinalRankingController() {
        System.out.println("[DEBUG] FinalRankingController constructor called");
    }

    // Setter for service injection
    public void setFinalRankingService(FinalRankingServiceImpl service) {
        this.finalRankingService = service;
        System.out.println("[DEBUG] FinalRankingServiceImpl injected: " + (service != null));
        populateJobs();
    }

    // Setter for HMService injection
    public void setHMService(HMService hmService) {
        this.hmService = hmService;
        System.out.println("[DEBUG] HMService injected: " + (hmService != null));
    }

    @FXML
    private void initialize() {
        System.out.println("[DEBUG] initialize() called");

        // Table column setup
        colRank.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getRank()));
        colApplicantName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getApplicantName()));
        colScore.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getCompositeScore()));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));

        // Initially disable the send button
        sendToHMButton.setDisable(true);

        if (finalRankingService != null) {
            System.out.println("[DEBUG] Service already injected, populating jobs...");
            populateJobs();
        } else {
            System.out.println("[DEBUG] Service not yet injected");
        }
    }

    private void populateJobs() {
        if (finalRankingService == null) {
            System.out.println("[ERROR] Service is null, cannot populate jobs");
            return;
        }

        if (jobComboBox == null) {
            System.out.println("[ERROR] jobComboBox is null, cannot populate jobs");
            return;
        }

        try {
            List<JobPosting> jobs = finalRankingService.getAllJobsForRecruiter();
            System.out.println("[DEBUG] Jobs fetched from service: " + jobs.size());
            for (JobPosting job : jobs) {
                System.out.println("[DEBUG] Job: " + job);
            }

            jobComboBox.setItems(FXCollections.observableArrayList(jobs));

            if (!jobs.isEmpty()) {
                jobComboBox.getSelectionModel().selectFirst(); // optionally pre-select
            } else {
                System.out.println("[WARNING] No jobs found for recruiter");
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Exception while fetching jobs");
            e.printStackTrace();
        }
    }

    @FXML
    private void generateFinalRanking() {
        JobPosting job = jobComboBox.getValue();
        if (job == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a job.");
            System.out.println("[WARNING] No job selected in ComboBox");
            return;
        }

        System.out.println("[DEBUG] Selected job: " + job);

        try {
            List<FinalRankedCandidate> finalList = finalRankingService.generateFinalRankingForJob(job.getId());
            finalRankingTable.setItems(FXCollections.observableArrayList(finalList));

            // Enable send to HM button if there are candidates
            sendToHMButton.setDisable(finalList.isEmpty());

            showAlert(Alert.AlertType.INFORMATION, "Final ranking generated successfully!");
            System.out.println("[DEBUG] Final ranking generated, candidates: " + finalList.size());
        } catch (IllegalStateException ex) {
            showAlert(Alert.AlertType.WARNING, ex.getMessage());
            System.out.println("[WARNING] IllegalStateException: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An unexpected error occurred while generating the final ranking.");
        }
    }

    @FXML
    private void sendToHiringManager() {
        System.out.println("[DEBUG Controller] sendToHiringManager button clicked");

        JobPosting job = jobComboBox.getValue();
        if (job == null) {
            System.out.println("[DEBUG Controller] No job selected");
            showAlert(Alert.AlertType.WARNING, "Please select a job.");
            return;
        }

        if (hmService == null) {
            System.out.println("[DEBUG Controller] hmService is NULL!");
            showAlert(Alert.AlertType.ERROR, "HM Service not available.");
            return;
        }

        System.out.println("[DEBUG Controller] Calling hmService.sendToHiringManager for job ID: " + job.getId());

        try {
            boolean success = hmService.sendToHiringManager(job.getId());
            System.out.println("[DEBUG Controller] hmService.sendToHiringManager returned: " + success);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Candidates sent to Hiring Manager successfully!");
                sendToHMButton.setDisable(true);
                generateFinalRanking();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to send candidates to Hiring Manager.");
            }
        } catch (Exception ex) {
            System.err.println("[DEBUG Controller] Exception in sendToHiringManager: " + ex.getMessage());
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred while sending candidates to HM: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}