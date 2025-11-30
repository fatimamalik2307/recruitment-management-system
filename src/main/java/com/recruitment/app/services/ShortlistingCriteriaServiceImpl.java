package com.recruitment.app.services;

import com.recruitment.app.dao.ShortlistingCriteriaDAO;
import com.recruitment.app.models.ShortlistingCriteria;
import com.recruitment.app.services.ShortlistingCriteriaService;

public class ShortlistingCriteriaServiceImpl implements ShortlistingCriteriaService {

    private final ShortlistingCriteriaDAO dao;

    public ShortlistingCriteriaServiceImpl(ShortlistingCriteriaDAO dao) {
        this.dao = dao;
    }

    @Override
    public ShortlistingCriteria createCriteria(ShortlistingCriteria criteria) {
        return dao.save(criteria);
    }

    @Override
    public ShortlistingCriteria getCriteriaById(int id) {
        return dao.getById(id);
    }

    @Override
    public ShortlistingCriteria getCriteriaByJobId(int jobId) {
        return dao.getByJobId(jobId);
    }

    @Override
    public boolean updateCriteria(ShortlistingCriteria criteria) {
        return dao.update(criteria);
    }

    @Override
    public boolean deleteCriteria(int id) {
        return dao.delete(id);
    }
}
