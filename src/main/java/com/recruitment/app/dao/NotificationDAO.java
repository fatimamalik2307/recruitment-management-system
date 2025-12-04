package com.recruitment.app.dao;

import com.recruitment.app.models.Notification;
import java.util.List;

public interface NotificationDAO {

    // Save an in–system notification
    Notification save(Notification note);

    // Load notifications for a specific user
    List<Notification> findByUser(int userId);

    // Mark notification as read
    boolean markAsRead(long notificationId);

    // Delete notification
    boolean delete(long id);

    // Queue email for retry when sending fails
    void queueEmail(String email, String subject, String message);
}
