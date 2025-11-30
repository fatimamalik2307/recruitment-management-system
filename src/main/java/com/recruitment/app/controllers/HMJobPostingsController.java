//package com.recruitment.app.controllers;
//
//import com.recruitment.app.models.JobPosting;
//import com.recruitment.app.services.HMService;
//import com.recruitment.app.services.JobService;
//import javafx.beans.property.ReadOnlyObjectWrapper;
//import javafx.beans.property.ReadOnlyStringWrapper;
//import javafx.collections.FXCollections;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.util.List;
//
//public class HMJobPostingsController {
//
//    @FXML private TableView<JobPosting> jobTable;
//    @FXML private TableColumn<JobPosting, String> colTitle;
//    @FXML private TableColumn<JobPosting, String> colDepartment;
//    @FXML private TableColumn<JobPosting, String> colStatus;
//    @FXML private TableColumn<JobPosting, Integer> colCandidates;
//    @FXML private TableColumn<JobPosting, String> colRecruiterStatus;
//
//    @FXML private Button reviewCandidatesButton;
//    @FXML private Label statusLabel;
//
//    private JobService jobService;
//    private HMService hmService;
//    private int currentUserId;
//
//    public void setJobService(JobService jobService) {
//        this.jobService = jobService;
//        loadJobPostings();
//    }
//
//    public void setHmService(HMService hmService) {
//        this.hmService = hmService;
//    }
//
//    public void setCurrentUserId(int userId) {
//        this.currentUserId = userId;
//    }
//
//    @FXML
//    private void initialize() {
//        colTitle.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getJobTitle()));
//        colDepartment.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDepartment()));
//        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));
//
//        colCandidates.setCellValueFactory(data -> {
//            if (hmService != null && data.getValue() != null) {
//                int count = hmService.getCandidatesSentToHM(data.getValue().getId()).size();
//                return new ReadOnlyObjectWrapper<>(count);
//            }
//            return new ReadOnlyObjectWrapper<>(0);
//        });
//
//        colRecruiterStatus.setCellValueFactory(data -> {
//            if (hmService != null && data.getValue() != null) {
//                boolean sent = hmService.hasRecruiterSubmittedFinalList(data.getValue().getId());
//                return new ReadOnlyStringWrapper(sent ? "Sent for Review" : "Not Sent");
//            }
//            return new ReadOnlyStringWrapper("Not Sent");
//        });
//
//        jobTable.getSelectionModel().selectedItemProperty().addListener(
//                (obs, oldJob, newJob) -> onJobSelected(newJob)
//        );
//
//        reviewCandidatesButton.setDisable(true);
//    }
//
//    private void loadJobPostings() {
//        if (jobService != null) {
//            List<JobPosting> jobs = jobService.getJobsByHiringManager(currentUserId);
//            jobTable.setItems(FXCollections.observableArrayList(jobs));
//            updateStatusLabel();
//        }
//    }
//
//    private void onJobSelected(JobPosting job) {
//        if (job != null) {
//            boolean canReview = hmService.hasRecruiterSubmittedFinalList(job.getId());
//            reviewCandidatesButton.setDisable(!canReview);
//            updateStatusLabel();
//        } else {
//            reviewCandidatesButton.setDisable(true);
//            statusLabel.setText("Select a job to view details");
//        }
//    }
//
//    private void updateStatusLabel() {
//        JobPosting selectedJob = jobTable.getSelectionModel().getSelectedItem();
//        if (selectedJob != null) {
//            boolean sent = hmService.hasRecruiterSubmittedFinalList(selectedJob.getId());
//            int candidateCount = hmService.getCandidatesSentToHM(selectedJob.getId()).size();
//            if (sent && candidateCount > 0) {
//                statusLabel.setText("✓ " + candidateCount + " candidates sent for review");
//            } else {
//                statusLabel.setText("⏳ Waiting for recruiter to send candidates");
//            }
//        }
//    }
//
//    @FXML
//    private void reviewCandidates() {
//        JobPosting selectedJob = jobTable.getSelectionModel().getSelectedItem();
//        if (selectedJob == null) {
//            showAlert(Alert.AlertType.WARNING, "Please select a job posting.");
//            return;
//        }
//
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/HMCandidateReview.fxml"));
//            Stage stage = new Stage();
//            stage.setScene(new Scene(loader.load()));
//            stage.setTitle("Review Candidates - " + selectedJob.getJobTitle());
//
//            HMCandidateReviewController controller = loader.getController();
//            controller.setJobPosting(selectedJob);
//            controller.setHMService(hmService);
//            controller.setCurrentUserId(currentUserId);
//            controller.loadCandidates(); // Preload candidates
//
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Failed to open candidate review: " + e.getMessage());
//        }
//    }
//
//    private void showAlert(Alert.AlertType type, String msg) {
//        Alert alert = new Alert(type, msg, ButtonType.OK);
//        alert.showAndWait();
//    }
//}
