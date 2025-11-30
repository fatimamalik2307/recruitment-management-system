package com.recruitment.app.dao;

import com.recruitment.app.models.ShortlistingCriteria;

public interface ShortlistingCriteriaDAO {

    ShortlistingCriteria save(ShortlistingCriteria criteria);

    ShortlistingCriteria getById(int id);

    ShortlistingCriteria getByJobId(int jobId);

    boolean update(ShortlistingCriteria criteria);

    boolean delete(int id);
}
