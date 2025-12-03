package com.recruitment.app.controllers;

import com.recruitment.app.models.Application;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.Shortlist;
import com.recruitment.app.models.ShortlistingCriteria;
import com.recruitment.app.services.*;
import com.recruitment.app.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReviewShortlistController {

    @FXML private ComboBox<JobPosting> jobComboBox;
    @FXML private TextArea criteriaDetails;
    @FXML private Button generateBtn;
    @FXML private TableView<Shortlist> shortlistTable;
    @FXML private TableColumn<Shortlist, Number> colId;
    @FXML private TableColumn<Shortlist, Number> colAppId;
    @FXML private TableColumn<Shortlist, String> colShortlistedAt;
    @FXML private TableColumn<Shortlist, String> colApplicantName;
    @FXML private TableColumn<Shortlist, String> colPhone;
    @FXML private TableColumn<Shortlist, Void> colActions;

    private ShortlistService shortlistService;
    private ShortlistingCriteriaService criteriaService;
    private RecruiterService recruiterService;
    private AssessmentService assessmentService;
    private FinalRankingService finalRankedService;


    public ReviewShortlistController() {}

    public void setServices(ShortlistService shortlistService,
                            ShortlistingCriteriaService criteriaService,
                            RecruiterService recruiterService,AssessmentService assessmentService,FinalRankingService finalRankedService)
    {
        this.shortlistService = shortlistService;
        this.criteriaService = criteriaService;
        this.recruiterService = recruiterService;
        this.assessmentService = assessmentService;
        this.finalRankedService = finalRankedService;

        initializeData();
    }

    private void initializeData() {
        int recruiterId = SessionManager.loggedInUser.getId();

        List<JobPosting> jobs = shortlistService.getJobsEligibleForShortlisting();
        jobComboBox.getItems().addAll(jobs);

        colId.setCellValueFactory(data -> javafx.beans.binding.Bindings.createIntegerBinding(data.getValue()::getId));
        colAppId.setCellValueFactory(data -> javafx.beans.binding.Bindings.createIntegerBinding(data.getValue()::getApplicationId));
        colShortlistedAt.setCellValueFactory(data ->
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> data.getValue().getShortlistedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                )
        );

        colApplicantName.setCellValueFactory(data -> {
            Application app = recruiterService.getApplicationById(data.getValue().getApplicationId());
            return new SimpleStringProperty(recruiterService.getApplicantName(app.getUserId()));
        });

        colPhone.setCellValueFactory(data -> {
            Application app = recruiterService.getApplicationById(data.getValue().getApplicationId());
            return new SimpleStringProperty(recruiterService.getApplicantPhone(app.getUserId()));
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View Details");
            private final Button assessBtn = new Button("Fill Assessment");
            private final HBox hbox = new HBox(10, viewBtn, assessBtn);

            {
                viewBtn.setOnAction(e -> {
                    Shortlist s = getTableView().getItems().get(getIndex());
                    openApplicationDetails(s.getApplicationId());
                });
                assessBtn.setOnAction(e -> {
                    Shortlist s = getTableView().getItems().get(getIndex());
                    openAssessmentForm(s.getId(), s.getApplicationId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        jobComboBox.setOnAction(e -> loadCriteria());
    }

    private void loadCriteria() {
        JobPosting job = jobComboBox.getValue();
        if (job == null) return;

        ShortlistingCriteria criteria = criteriaService.getCriteriaByJobId(job.getId());
        if (criteria == null) {
            criteriaDetails.setText("No criteria found for this job!");
            generateBtn.setDisable(true);
            return;
        }

        generateBtn.setDisable(false);

        StringBuilder sb = new StringBuilder();
        sb.append("Required Qualification: ").append(criteria.getRequiredQualification()).append("\n");
        sb.append("Required Skills: ").append(criteria.getRequiredSkills()).append("\n");
        sb.append("Min Experience: ").append(criteria.getMinExperience()).append("\n");
        sb.append("Optional Location: ").append(criteria.getOptionalLocation()).append("\n");
        sb.append("Optional Grade: ").append(criteria.getOptionalGrade()).append("\n");
        sb.append("Created At: ").append(criteria.getCreatedAt()).append("\n");

        criteriaDetails.setText(sb.toString());
    }

    @FXML
    private void generateShortlist() {
        JobPosting job = jobComboBox.getValue();
        if (job == null) {
            new Alert(Alert.AlertType.ERROR, "Select a job first!").show();
            return;
        }

        // --- CHECK IF FINAL RANKING IS ALREADY GENERATED ---
        if (finalRankedService.existsForJob(job.getId())) {
            new Alert(Alert.AlertType.WARNING,
                    "Final ranking has already been generated for this job.\nShortlist cannot be viewed or generated again.")
                    .show();
            return;
        }

        // --- CONFIRMATION: CLOSE JOB? ---
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Generating shortlist will CLOSE this job.\nApplicants will no longer see it.\n\nContinue?",
                ButtonType.YES, ButtonType.NO);

        confirm.setTitle("Confirm Operation");
        confirm.showAndWait();

        if (confirm.getResult() != ButtonType.YES) return;

        // --- CALL SERVICE ---
        List<Shortlist> list = shortlistService.generateShortlist(job.getId());

        if (list == null) {
            // Means ranking exists
            new Alert(Alert.AlertType.WARNING,
                    "Final ranking already exists. Cannot generate or view shortlist.")
                    .show();
            return;
        }

        if (list.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "No applicants met the criteria.")
                    .show();
            return;
        }

        shortlistTable.setItems(FXCollections.observableList(list));

        new Alert(Alert.AlertType.INFORMATION, "Shortlist loaded successfully!").show();
    }

    private void openApplicationDetails(int applicationId) {
        Application app = recruiterService.getApplicationById(applicationId);
        if (app == null) {
            new Alert(Alert.AlertType.ERROR, "Application not found!").show();
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Qualification: ").append(app.getQualification()).append("\n");
        details.append("Experience: ").append(app.getExperience()).append("\n");
        details.append("Cover Letter: ").append(app.getCoverLetter()).append("\n");
        details.append("Status: ").append(app.getStatus()).append("\n");
        details.append("Applied At: ").append(app.getAppliedAt()).append("\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Application Details");
        alert.setHeaderText("Applicant: " + recruiterService.getApplicantName(app.getUserId()));
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    private void openAssessmentForm(int shortlistId, int applicationId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AssessmentForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            AssessmentFormController controller = loader.getController();
            controller.setData(shortlistId, applicationId, recruiterService,assessmentService);

            stage.setTitle("Fill Assessment");
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
