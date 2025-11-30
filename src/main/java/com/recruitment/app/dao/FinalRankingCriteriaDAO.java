package com.recruitment.app.dao;

import com.recruitment.app.models.FinalRankingCriteria;

public interface FinalRankingCriteriaDAO {
    boolean save(FinalRankingCriteria criteria);
    FinalRankingCriteria getByJobId(int jobId);
    boolean update(FinalRankingCriteria criteria);
}
