package com.recruitment.app.dao;

import com.recruitment.app.models.PersonSpecification;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class PersonSpecificationDAOImpl implements PersonSpecificationDAO {

    private final Connection conn;

    public PersonSpecificationDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(PersonSpecification spec) {
        String sql = "INSERT INTO person_specifications (recruiter_id, skills, experience, education, traits) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, spec.getRecruiterId());
            ps.setString(2, spec.getSkills());
            ps.setString(3, spec.getExperience());
            ps.setString(4, spec.getEducation());
            ps.setString(5, spec.getTraits());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
