package com.recruitment.app.services;

import com.recruitment.app.dao.NotificationDAO;
import com.recruitment.app.models.Notification;
import com.recruitment.app.models.User;
import com.recruitment.app.utils.EmailService;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;
    private final UserService userService;
    private final EmailService emailService;

    public NotificationServiceImpl(NotificationDAO dao,
                                   UserService userService,
                                   EmailService emailService) {
        this.notificationDAO = dao;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public void sendNotificationToApplicant(int applicantId, String subject, String message) {

        // Save notification for in-app alerts
        Notification notification = new Notification(applicantId, subject, message);
        notificationDAO.save(notification);

        // Send Email
        User user = userService.getUserById(applicantId);
        if (user != null && user.getEmail() != null) {
            sendEmail(user.getEmail(), subject, message);
        } else {
            System.err.println("User email not found. Only in-app notification saved.");
        }
    }

    @Override
    public void sendEmail(String toEmail, String subject, String message) {
        try {
            emailService.sendEmail(toEmail, subject, message);
        } catch (Exception e) {
            System.err.println("Email sending failed. Queuing email for retry later...");
            notificationDAO.queueEmail(toEmail, subject, message);
        }
    }
}
