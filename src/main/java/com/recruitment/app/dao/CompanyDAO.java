package com.recruitment.app.dao;

public interface CompanyDAO {
    /**
     * Returns company id for existing company, or creates it and returns new id.
     * Returns -1 on error.
     */
    int getOrCreateCompanyId(String companyName);
}
