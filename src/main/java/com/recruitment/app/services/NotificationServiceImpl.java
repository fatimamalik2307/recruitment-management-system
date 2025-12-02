package com.recruitment.app.services;

import com.recruitment.app.models.Application;
import com.recruitment.app.dao.ApplicationDAO;
import com.recruitment.app.utils.EmailService;

import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final ApplicationDAO applicationDAO;

    public NotificationServiceImpl(ApplicationDAO dao) {
        this.applicationDAO = dao;
    }

    @Override
    public void notifyCandidates(List<Application> rankings) {

        for (Application app : rankings) {
            String email = applicationDAO.getApplicantEmail(app.getUserId());

            boolean isSelected = app.getStatus().equalsIgnoreCase("selected");

            String subject = isSelected
                    ? "Congratulations! You are Selected"
                    : "Update on Your Application";

            String message = isSelected
                    ? "Dear Candidate,\n\nYou have been selected for the position you applied for!"
                    : "Dear Candidate,\n\nThank you for applying. Unfortunately, you were not selected.";

            boolean success = EmailService.sendEmail(email, subject, message);

            if (!success) {
                System.out.println("Email failed → placing in retry queue");
                // TODO: Add retry queue / log failure
            }
        }
    }
}
