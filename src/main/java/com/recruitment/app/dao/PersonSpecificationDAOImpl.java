package com.recruitment.app.dao;

import com.recruitment.app.models.PersonSpecification;

import java.sql.*;

public class PersonSpecificationDAOImpl implements PersonSpecificationDAO {

    private final Connection conn;

    public PersonSpecificationDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(PersonSpecification spec) {
        String sql = "INSERT INTO person_specifications (recruiter_id, skills, experience, education, traits) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, spec.getRecruiterId());
            ps.setString(2, spec.getSkills());
            ps.setString(3, spec.getExperience());
            ps.setString(4, spec.getEducation());
            ps.setString(5, spec.getTraits());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public PersonSpecification getLatestByRecruiter(int recruiterId) {
        String sql = "SELECT * FROM person_specifications WHERE recruiter_id = ? ORDER BY created_at DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recruiterId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private PersonSpecification mapRow(ResultSet rs) throws SQLException {
        PersonSpecification p = new PersonSpecification();
        p.setId(rs.getInt("id"));
        p.setRecruiterId(rs.getInt("recruiter_id"));
        p.setSkills(rs.getString("skills"));
        p.setExperience(rs.getString("experience"));
        p.setEducation(rs.getString("education"));
        p.setTraits(rs.getString("traits"));
        return p;
    }
}
