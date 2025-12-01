package com.recruitment.app.services;

import com.recruitment.app.models.PersonSpecification;

public interface PersonSpecificationService {

    boolean save(PersonSpecification spec);

    PersonSpecification getLatestByRecruiter(int recruiterId);
}
