package com.recruitment.app.models;

import java.time.LocalDateTime;

public class Notification {

    private Long id;
    private Integer userId;
    private String subject;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;

    // --- Constructor for new notifications ---
    public Notification(Integer userId, String subject, String message) {
        this.userId = userId;
        this.subject = subject;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    // --- Full constructor (used by DAO when loading from DB) ---
    public Notification(Long id, Integer userId, String subject, String message,
                        LocalDateTime createdAt, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.subject = subject;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
