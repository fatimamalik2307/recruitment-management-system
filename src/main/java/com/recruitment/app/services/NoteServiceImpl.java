package com.recruitment.app.services;

import com.recruitment.app.dao.ApplicantNoteDAO;
import com.recruitment.app.models.ApplicantNote;
import java.util.List;

public class NoteServiceImpl implements NoteService {
    private final ApplicantNoteDAO noteDAO;

    public NoteServiceImpl(ApplicantNoteDAO noteDAO) {
        this.noteDAO = noteDAO;
    }

    @Override
    public List<ApplicantNote> getNotesByApplication(Long applicationId) {
        if (applicationId == null) {
            throw new IllegalArgumentException("Application ID cannot be null");
        }
        return noteDAO.findByApplicationId(applicationId);
    }

    @Override
    public List<ApplicantNote> getNotesByRecruiter(Long recruiterId) {
        if (recruiterId == null) {
            throw new IllegalArgumentException("Recruiter ID cannot be null");
        }
        return noteDAO.findByRecruiterId(recruiterId);
    }

    @Override
    public ApplicantNote createNote(ApplicantNote note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }
        if (note.getApplicationId() == null) {
            throw new IllegalArgumentException("Application ID is required");
        }
        if (note.getRecruiterId() == null) {
            throw new IllegalArgumentException("Recruiter ID is required");
        }
        if (note.getNoteText() == null || note.getNoteText().trim().isEmpty()) {
            throw new IllegalArgumentException("Note text cannot be empty");
        }

        return noteDAO.save(note);
    }

    @Override
    public ApplicantNote updateNote(ApplicantNote note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }
        if (note.getId() == null) {
            throw new IllegalArgumentException("Note ID is required for update");
        }
        if (note.getNoteText() == null || note.getNoteText().trim().isEmpty()) {
            throw new IllegalArgumentException("Note text cannot be empty");
        }

        return noteDAO.update(note);
    }

    @Override
    public boolean deleteNote(Long noteId) {
        if (noteId == null) {
            throw new IllegalArgumentException("Note ID cannot be null");
        }
        return noteDAO.delete(noteId);
    }

    @Override
    public boolean deleteNotesByApplication(Long applicationId) {
        if (applicationId == null) {
            throw new IllegalArgumentException("Application ID cannot be null");
        }
        return noteDAO.deleteByApplicationId(applicationId);
    }
}