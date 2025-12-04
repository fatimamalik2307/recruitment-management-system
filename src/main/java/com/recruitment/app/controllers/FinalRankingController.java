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

    // Services - will be injected by ControllerFactory
    private FinalRankingService finalRankingService;
    private HMService hmService;


    private int selectedJobId; // For notification purposes

    // ---------- DEFAULT CONSTRUCTOR ----------
    public FinalRankingController() {
        // Empty - services will be injected via setters
    }

    // ---------- SERVICE INJECTION ----------
    public void setServices(FinalRankingService finalRankingService,
                            HMService hmService,
                            NotificationService notificationService) {
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


        // Don't populate jobs here - wait for services to be injected
        // We'll populate jobs when services are available
    }

    // Call this after services are injected
    public void initializeAfterInjection() {
        System.out.println("DEBUG: initializeAfterInjection called");
        if (finalRankingService != null) {
            System.out.println("DEBUG: finalRankingService is injected");
            populateJobs();
        } else {
            System.err.println("ERROR: FinalRankingService not injected!");
        }
    }

    private void populateJobs() {
        System.out.println("DEBUG: populateJobs called");
        try {
            List<JobPosting> jobs = finalRankingService.getJobsEligibleForFinalRanking();
            System.out.println("DEBUG: Jobs fetched: " + (jobs == null ? "null" : jobs.size()));
            if (jobs != null) {
                for (JobPosting job : jobs) {
                    System.out.println("DEBUG: Job -> " + job.getTitle() + " (ID: " + job.getId() + ")");
                }
            }
            jobComboBox.setItems(FXCollections.observableArrayList(jobs));
            if (!jobs.isEmpty()) jobComboBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to load jobs.");
        }
    }

    @FXML
    private void generateFinalRanking() {
        System.out.println("DEBUG: generateFinalRanking called");
        if (finalRankingService == null) {
            showAlert(Alert.AlertType.ERROR, "Service not initialized!");
            return;
        }

        if (jobComboBox.getItems().isEmpty()) {
            System.out.println("DEBUG: jobComboBox is empty, calling populateJobs()");
            populateJobs(); // populate if empty
        }

        JobPosting job = jobComboBox.getValue();
        System.out.println("DEBUG: Selected job -> " + (job == null ? "null" : job.getTitle()));

        if (job == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a job.");
            return;
        }

        try {
            List<FinalRankedCandidate> finalList = finalRankingService.generateFinalRankingForJob(job.getId());
            System.out.println("DEBUG: Final ranking list size -> " + (finalList == null ? "null" : finalList.size()));
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
        // ADD null check for service
        if (hmService == null) {
            showAlert(Alert.AlertType.ERROR, "Service not initialized!");
            return;
        }

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



    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}