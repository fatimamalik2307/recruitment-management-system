package com.recruitment.app.models;

import java.time.LocalDateTime;

public class ApplicantNote {
    private Long id;
    private Long applicationId;
    private Long recruiterId;
    private String noteText;
    private NoteType noteType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum NoteType {
        INTERNAL, FEEDBACK, INTERVIEW
    }

    // Constructors
    public ApplicantNote() {}

    public ApplicantNote(Long applicationId, Long recruiterId, String noteText, NoteType noteType) {
        this.applicationId = applicationId;
        this.recruiterId = recruiterId;
        this.noteText = noteText;
        this.noteType = noteType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getRecruiterId() { return recruiterId; }
    public void setRecruiterId(Long recruiterId) { this.recruiterId = recruiterId; }

    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }

    public NoteType getNoteType() { return noteType; }
    public void setNoteType(NoteType noteType) { this.noteType = noteType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}