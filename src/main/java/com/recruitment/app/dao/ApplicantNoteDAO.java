package com.recruitment.app.dao;

import com.recruitment.app.models.ApplicantNote;
import java.util.List;
import java.util.Optional;

public interface ApplicantNoteDAO {
    Optional<ApplicantNote> findById(Long id);
    List<ApplicantNote> findByApplicationId(Long applicationId);
    List<ApplicantNote> findByRecruiterId(Long recruiterId);
    List<ApplicantNote> findByApplicationAndRecruiter(Long applicationId, Long recruiterId);
    ApplicantNote save(ApplicantNote note);
    ApplicantNote update(ApplicantNote note);
    boolean delete(Long id);
    boolean deleteByApplicationId(Long applicationId);
    // Add to ApplicantNoteDAO interface
    List<ApplicantNote> findByHiringManagerId(Long hiringManagerId);
    List<ApplicantNote> findByApplicationAndType(Long applicationId, String noteType);
    int countNotesByApplication(Long applicationId);
}