package com.recruitment.app.controllers;

import com.recruitment.app.config.DBConnection;
import com.recruitment.app.dao.ApplicationDAO;
import com.recruitment.app.dao.ApplicationDAOImpl;
import com.recruitment.app.models.Application;
import com.recruitment.app.models.FinalRankedCandidate;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.services.FinalRankingServiceImpl;
import com.recruitment.app.services.HMService;
import com.recruitment.app.services.NotificationService;
import com.recruitment.app.services.NotificationServiceImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
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

    private FinalRankingServiceImpl finalRankingService;
    private HMService hmService;

    // Added for UC-28
    private ApplicationDAO applicationDAO =
            new ApplicationDAOImpl(DBConnection.getConnection());

    private NotificationService notificationService =
            new NotificationServiceImpl(applicationDAO);

    private int selectedJobId;

    public void setFinalRankingService(FinalRankingServiceImpl service) {
        this.finalRankingService = service;
        populateJobs();
    }

    public void setHMService(HMService hmService) {
        this.hmService = hmService;
    }

    @FXML
    private void initialize() {
        colRank.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getRank()));
        colApplicantName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getApplicantName()));
        colScore.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getCompositeScore()));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));

        sendToHMButton.setDisable(true);
    }

    private void populateJobs() {
        if (finalRankingService == null) return;

        List<JobPosting> jobs = finalRankingService.getAllJobsForRecruiter();
        jobComboBox.setItems(FXCollections.observableArrayList(jobs));
    }

    @FXML
    private void generateFinalRanking() {
        JobPosting job = jobComboBox.getValue();
        if (job == null) {
            showAlert("Please select a job.");
            return;
        }

        selectedJobId = job.getId();  // 🔥 IMPORTANT FOR NOTIFY FEATURE

        List<FinalRankedCandidate> finalList =
                finalRankingService.generateFinalRankingForJob(job.getId());

        finalRankingTable.setItems(FXCollections.observableArrayList(finalList));
        sendToHMButton.setDisable(finalList.isEmpty());

        showAlert("Final ranking generated successfully!");
    }

    @FXML
    private void sendToHiringManager() {
        JobPosting job = jobComboBox.getValue();
        if (job == null) {
            showAlert("Please select a job.");
            return;
        }

        boolean success = hmService.sendToHiringManager(job.getId());

        if (success) {
            showAlert("Candidates sent to Hiring Manager successfully!");
        } else {
            showAlert("Failed to send candidates to HM.");
        }
    }

    // ---------------------------------
    // UC-28: Notify Candidates
    // ---------------------------------
    @FXML
    private void notifyCandidates() {
        if (selectedJobId == 0) {
            showAlert("Generate final ranking before notifying candidates.");
            return;
        }

        List<Application> finalRankings =
                applicationDAO.getFinalRankingsByJob(selectedJobId);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Notify ALL candidates of their final decision?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {

            notificationService.notifyCandidates(finalRankings);

            showAlert("All candidate notifications sent!");
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
