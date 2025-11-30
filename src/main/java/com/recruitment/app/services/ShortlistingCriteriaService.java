package com.recruitment.app.services;

import com.recruitment.app.models.ShortlistingCriteria;

public interface ShortlistingCriteriaService {

    ShortlistingCriteria createCriteria(ShortlistingCriteria criteria);

    ShortlistingCriteria getCriteriaById(int id);

    ShortlistingCriteria getCriteriaByJobId(int jobId);

    boolean updateCriteria(ShortlistingCriteria criteria);

    boolean deleteCriteria(int id);
}
