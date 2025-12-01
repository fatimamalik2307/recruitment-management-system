package com.recruitment.app.services;

import com.recruitment.app.dao.PersonSpecificationDAO;
import com.recruitment.app.models.PersonSpecification;

public class PersonSpecificationServiceImpl implements PersonSpecificationService {

    private final PersonSpecificationDAO dao;

    public PersonSpecificationServiceImpl(PersonSpecificationDAO dao) {
        this.dao = dao;
    }

    @Override
    public boolean save(PersonSpecification spec) {
        return dao.save(spec);
    }

    @Override
    public PersonSpecification getLatestByRecruiter(int recruiterId) {
        return dao.getLatestByRecruiter(recruiterId);
    }
}
