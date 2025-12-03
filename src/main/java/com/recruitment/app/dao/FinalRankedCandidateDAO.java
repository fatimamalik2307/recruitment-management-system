package com.recruitment.app.dao;

import com.recruitment.app.models.FinalRankedCandidate;
import java.util.List;
import java.util.Optional;

public interface FinalRankedCandidateDAO {

    // Save a new candidate
    boolean save(FinalRankedCandidate candidate);

    // Get all candidates by job id, ordered by rank
    List<FinalRankedCandidate> getByJobId(int jobId);

    // Get candidate by ID
    FinalRankedCandidate getById(int id);

    // Update rank and composite score (used by recruiter)
    void updateRankAndScore(int id, double compositeScore, int rank);

    // Update hiring manager notes and status
    boolean updateStatusAndNotes(int id, String status, String hmNotes);

    // Delete candidate by ID
    void delete(int id);

    // UPDATED HM METHODS - CHANGED Long TO int
    List<FinalRankedCandidate> findByJobPostingIdAndStatus(int jobPostingId, String status);
    boolean updateStatus(int id, String status);
    boolean updateStatusByJobPosting(int jobPostingId, String status);
    boolean existsByJobPostingId(int jobPostingId);
    Optional<FinalRankedCandidate> findById(int id);
    Optional<FinalRankedCandidate> findByApplicationId(int applicationId);
    List<FinalRankedCandidate> findByHiringManagerId(int hiringManagerId);

    boolean existsForJob(int jobId);
}