package com.recruitment.app.services;

public interface NotificationService {

    /**
     * Sends an in-system + email notification to a specific applicant.
     */
    void sendNotificationToApplicant(int applicantId, String subject, String message);

    /**
     * Sends an email. If it fails, queues for retry.
     */
    void sendEmail(String toEmail, String subject, String message);
}
