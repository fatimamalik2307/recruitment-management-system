package com.recruitment.app.services;

import com.recruitment.app.models.*;

import java.util.List;

public interface FinalRankingService {
    void setRankingCriteria(FinalRankingCriteria criteria);
    FinalRankingCriteria getRankingCriteria(int jobId);

    List<FinalRankedCandidate> generateFinalRanking(
            JobPosting job,
            List<AssessmentResult> assessments,
            List<Shortlist> shortlists, double technicalWeight,
            double hrWeight);

    List<FinalRankedCandidate> getFinalRankingByJob(int jobId);
    void saveFinalRanking(List<FinalRankedCandidate> finalList);
    void flagListReady(int jobId);
    void updateStatusAndNotes(int candidateId, String status, String hmNotes);
}

