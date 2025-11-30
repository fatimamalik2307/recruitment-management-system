package com.recruitment.app.services;

import com.recruitment.app.models.ApplicantNote;
import java.util.List;

public interface NoteService {
    List<ApplicantNote> getNotesByApplication(Long applicationId);
    List<ApplicantNote> getNotesByRecruiter(Long recruiterId);
    ApplicantNote createNote(ApplicantNote note);
    ApplicantNote updateNote(ApplicantNote note);
    boolean deleteNote(Long noteId);
    boolean deleteNotesByApplication(Long applicationId);
}