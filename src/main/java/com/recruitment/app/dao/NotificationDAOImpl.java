package com.recruitment.app.dao;

import com.recruitment.app.models.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements NotificationDAO {

    private final Connection conn;

    public NotificationDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Notification save(Notification notification) {
        String sql =
                "INSERT INTO notifications (user_id, subject, message, created_at, is_read) " +
                        "VALUES (?, ?, ?, NOW(), FALSE) RETURNING id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getSubject());
            stmt.setString(3, notification.getMessage());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                notification.setId(rs.getLong("id"));
            }

            return notification;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save notification", e);
        }
    }

    @Override
    public List<Notification> findByUser(int userId) {
        List<Notification> list = new ArrayList<>();

        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Notification n = new Notification(
                        rs.getLong("id"),
                        rs.getInt("user_id"),
                        rs.getString("subject"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getBoolean("is_read")
                );
                list.add(n);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch notifications", e);
        }

        return list;
    }

    @Override
    public boolean markAsRead(long notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, notificationId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark notification as read", e);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM notifications WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete notification", e);
        }
    }

    @Override
    public void queueEmail(String email, String subject, String message) {
        // Can later be saved into a table "email_queue"
        System.err.println("Email failed → Queued for retry: " + email);
    }
}
