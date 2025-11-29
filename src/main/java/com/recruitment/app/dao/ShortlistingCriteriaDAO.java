package com.recruitment.app.dao;

import com.recruitment.app.models.ShortlistingCriteria;

public interface ShortlistingCriteriaDAO {
    void save(ShortlistingCriteria criteria);
    ShortlistingCriteria getByJobId(int jobId);
}
