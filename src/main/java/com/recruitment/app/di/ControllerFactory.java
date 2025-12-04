package com.recruitment.app.di;

import com.recruitment.app.controllers.*;
import javafx.util.Callback;

public class ControllerFactory implements Callback<Class<?>, Object> {

    private final ServiceContainer services = ServiceContainer.getInstance();

    @Override
    public Object call(Class<?> controllerClass) {
        try {
            System.out.println("ControllerFactory: Creating " + controllerClass.getSimpleName());

            if (controllerClass == RecruiterDashboardController.class) {
                RecruiterDashboardController controller = new RecruiterDashboardController();
                controller.setServices(
                        services.getJobService(),
                        services.getCriteriaService(),
                        services.getShortlistService(),
                        services.getRecruiterService(),
                        services.getAssessmentService(),
                        services.getFinalRankingService(),
                        services.getHmService()
                );
                return controller;

            } else if (controllerClass == ReviewShortlistController.class) {
                ReviewShortlistController controller = new ReviewShortlistController();
                controller.setServices(
                        services.getShortlistService(),
                        services.getCriteriaService(),
                        services.getRecruiterService(),
                        services.getAssessmentService(),
                        services.getFinalRankingService()
                );
                return controller;

            } else if (controllerClass == ApplicationFormController.class) {
                ApplicationFormController controller = new ApplicationFormController();
                controller.setApplicationService(services.getApplicationService());
                return controller;

            } else if (controllerClass == ChangePasswordController.class) {
                ChangePasswordController controller = new ChangePasswordController();
                controller.setUserService(services.getUserService());
                return controller;

            } else if (controllerClass == AssessmentFormController.class) {
                AssessmentFormController controller = new AssessmentFormController();
                controller.setServices(
                        services.getRecruiterService(),
                        services.getAssessmentService()
                );
                return controller;

            } else if (controllerClass == CreateJobPostingController.class) {
                // KEEP ONLY ONE VERSION - with both services
                CreateJobPostingController controller = new CreateJobPostingController();
                controller.setJobService(services.getJobService());
                controller.setPersonSpecificationService(services.getPersonSpecificationService());
                return controller;

            } else if (controllerClass == HiringManagerDashboardController.class) {
                HiringManagerDashboardController controller = new HiringManagerDashboardController();
                controller.setServices(
                        services.getRecruiterService(),
                        services.getHmService(),
                        services.getNoteService(),
                        services.getJobService(),
                        services.getUserService()
                );
                return controller; // ADDED MISSING RETURN

            } else if (controllerClass == HMCandidateReviewController.class) {
                HMCandidateReviewController controller = new HMCandidateReviewController();
                controller.setServices(
                        services.getHmService(),
                        services.getRecruiterService(),
                        services.getUserService(),
                        services.getNoteService()

                );
                return controller;

            } else if (controllerClass == JobDescriptionController.class) {
                JobDescriptionController controller = new JobDescriptionController();
                controller.setJobDescriptionService(services.getJobDescriptionService());
                return controller;

            } else if (controllerClass == JobDescriptionListController.class) {
                JobDescriptionListController controller = new JobDescriptionListController();
                controller.setJobDescriptionService(services.getJobDescriptionService());
                return controller;

            } else if (controllerClass == JobDetailsController.class) {
                JobDetailsController controller = new JobDetailsController();
                controller.setApplicationService(services.getApplicationService());
                return controller;

            } else if (controllerClass == LoginController.class) {
                LoginController controller = new LoginController();
                controller.setUserService(services.getUserService());
                return controller;

            } else if (controllerClass == PersonSpecificationController.class) {
                PersonSpecificationController controller = new PersonSpecificationController();
                controller.setPersonSpecificationService(services.getPersonSpecificationService());
                return controller;

            } else if (controllerClass == RecruitmentReportController.class) {
                RecruitmentReportController controller = new RecruitmentReportController();
                controller.setServices(
                        services.getRecruitmentReportService(),
                        services.getJobService()
                );
                return controller;

            } else if (controllerClass == RegisterController.class) {
                RegisterController controller = new RegisterController();
                controller.setUserService(services.getUserService());
                return controller;

            } else if (controllerClass == ReviewApplicationsController.class) {
                ReviewApplicationsController controller = new ReviewApplicationsController();
                controller.setRecruiterService(services.getRecruiterService());
                return controller;

            } else if (controllerClass == ShortlistingCriteriaController.class) {
                ShortlistingCriteriaController controller = new ShortlistingCriteriaController();
                controller.setRecruiterService(services.getRecruiterService());
                return controller;

            } else if (controllerClass == TrackApplicationsController.class) {
                TrackApplicationsController controller = new TrackApplicationsController();
                controller.setApplicationService(services.getApplicationService());
                return controller;

            } else if (controllerClass == UpdateProfileController.class) {
                UpdateProfileController controller = new UpdateProfileController();
                controller.setUserService(services.getUserService());
                return controller;

            } else if (controllerClass == BrowseJobsController.class) {
                System.out.println("ControllerFactory: Creating BrowseJobsController with JobService injection");
                BrowseJobsController controller = new BrowseJobsController();
                controller.setJobService(services.getJobService());
                return controller;

            } else if (controllerClass == FinalRankingController.class) {
                System.out.println("ControllerFactory: Creating FinalRankingController with setter injection");
                FinalRankingController controller = new FinalRankingController();
                controller.setServices(
                        services.getFinalRankingService(),
                        services.getHmService(),
                        services.getNotificationService()
                );
                // REMOVED: controller.initializeAfterInjection(); // Don't call this here!
                return controller;

            }
            // Add these at the end before the default return
            else if (controllerClass == UploadDocumentsController.class) {
                UploadDocumentsController controller = new UploadDocumentsController();
                controller.setApplicationService(services.getApplicationService());
                return controller;
            }
            else if (controllerClass == WelcomeController.class) {
                return new WelcomeController(); // No services needed
            }
            // REMOVED DUPLICATE ApplicationFormController entry
            // REMOVED DUPLICATE CreateJobPostingController entry

            // For other controllers, use default constructor
            return controllerClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create controller: " + controllerClass.getName(), e);
        }
    }

    // Singleton instance
    private static ControllerFactory instance;
    public static ControllerFactory getInstance() {
        if (instance == null) {
            instance = new ControllerFactory();
        }
        return instance;
    }
}