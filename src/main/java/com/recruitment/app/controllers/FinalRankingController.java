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

    private FinalRankingService finalRankingService;
    private HMService hmService;
    private NotificationService notificationService;
    private int selectedJobId;

    // Track initialization state
    private boolean servicesInjected = false;
    private boolean uiInitialized = false;

    public FinalRankingController() {
        // Empty constructor
    }

    // Service injection method
    public void setServices(FinalRankingService finalRankingService,
                            HMService hmService,
                            NotificationService notificationService) {
        this.finalRankingService = finalRankingService;
        this.hmService = hmService;
        this.notificationService = notificationService;
        this.servicesInjected = true;

        // If UI is already initialized, populate jobs
        if (uiInitialized) {
            populateJobs();
        }
    }

    @FXML
    private void initialize() {
        System.out.println("DEBUG: FXML initialize() called");

        // Table column setup
        colRank.setCellValueFactory(data -> {
            System.out.println("DEBUG: Setting Rank for: " + data.getValue());
            return new ReadOnlyObjectWrapper<>(data.getValue().getRank());
        });

        colApplicantName.setCellValueFactory(data -> {
            String name = data.getValue().getApplicantName();
            System.out.println("DEBUG: Setting Applicant Name: " + name);
            return new ReadOnlyStringWrapper(name);
        });

        colScore.setCellValueFactory(data -> {
            System.out.println("DEBUG: Setting Score for: " + data.getValue());
            return new ReadOnlyObjectWrapper<>(data.getValue().getCompositeScore());
        });

        colStatus.setCellValueFactory(data -> {
            System.out.println("DEBUG: Setting Status for: " + data.getValue());
            return new ReadOnlyStringWrapper(data.getValue().getStatus());
        });

        sendToHMButton.setDisable(true);

        uiInitialized = true;

        if (servicesInjected) {
            populateJobs();
        } else {
            System.out.println("DEBUG: Services not yet injected, waiting...");
        }
    }

    @FXML
    private void generateFinalRanking() {
        System.out.println("DEBUG: generateFinalRanking called");

        if (finalRankingService == null) {
            System.out.println("DEBUG: finalRankingService is NULL!");
            showAlert(Alert.AlertType.ERROR, "Services not initialized. Please restart the application.");
            return;
        }

        if (jobComboBox.getItems().isEmpty()) {
            System.out.println("DEBUG: No jobs in combo box, trying to populate...");
            populateJobs();
            if (jobComboBox.getItems().isEmpty()) {
                System.out.println("DEBUG: Still no jobs after populateJobs");
                showAlert(Alert.AlertType.WARNING, "No jobs available for final ranking.");
                return;
            }
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

            if (finalList != null && !finalList.isEmpty()) {
                for (FinalRankedCandidate c : finalList) {
                    System.out.println("DEBUG: Candidate object: " + c);
                    System.out.println("DEBUG: Candidate Name: " + c.getApplicantName());
                }

                finalRankingTable.setItems(FXCollections.observableArrayList(finalList));
                sendToHMButton.setDisable(false);
                selectedJobId = job.getId();

                showAlert(Alert.AlertType.INFORMATION,
                        String.format("Final ranking generated successfully! %d candidates ranked.", finalList.size()));
            } else {
                System.out.println("DEBUG: No candidates returned from service!");
                finalRankingTable.setItems(FXCollections.observableArrayList());
                sendToHMButton.setDisable(true);

                showAlert(Alert.AlertType.INFORMATION, "No candidates found for final ranking.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "An error occurred while generating the final ranking: " + ex.getMessage());
        }
    }


    private void populateJobs() {
        System.out.println("DEBUG: populateJobs called");

        if (finalRankingService == null) {
            System.err.println("ERROR: finalRankingService is null in populateJobs");
            return;
        }

        try {
            List<JobPosting> jobs = finalRankingService.getJobsEligibleForFinalRanking();
            System.out.println("DEBUG: Jobs fetched: " + (jobs == null ? "null" : jobs.size()));

            if (jobs != null && !jobs.isEmpty()) {
                jobComboBox.setItems(FXCollections.observableArrayList(jobs));
                jobComboBox.getSelectionModel().selectFirst();

                for (JobPosting job : jobs) {
                    System.out.println("DEBUG: Job -> " + job.getTitle() + " (ID: " + job.getId() + ")");
                }
            } else {
                System.out.println("DEBUG: No jobs available for final ranking");
                jobComboBox.setItems(FXCollections.observableArrayList());
                showAlert(Alert.AlertType.INFORMATION, "No jobs are currently eligible for final ranking.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to load jobs: " + e.getMessage());
        }
    }



    @FXML
    private void sendToHiringManager() {
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
            showAlert(Alert.AlertType.ERROR, "An error occurred while sending candidates to HM: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}