package com.recruitment.app.dao;

import com.recruitment.app.models.ShortlistingCriteria;

import java.sql.*;
import java.time.LocalDateTime;

public class ShortlistingCriteriaDAOImpl implements ShortlistingCriteriaDAO {

    private final Connection conn;

    public ShortlistingCriteriaDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(ShortlistingCriteria criteria) {
        String sql = "INSERT INTO shortlisting_criteria (job_id, min_experience, required_qualification, required_skills, optional_location, optional_grade, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, criteria.getJobId());
            if (criteria.getMinExperience() != null) {
                ps.setInt(2, criteria.getMinExperience());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, criteria.getRequiredQualification());
            ps.setString(4, criteria.getRequiredSkills());
            ps.setString(5, criteria.getOptionalLocation());
            ps.setString(6, criteria.getOptionalGrade());
            ps.setTimestamp(7, Timestamp.valueOf(criteria.getCreatedAt() != null ? criteria.getCreatedAt() : LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ShortlistingCriteria getByJobId(int jobId) {
        String sql = "SELECT * FROM shortlisting_criteria WHERE job_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ShortlistingCriteria criteria = new ShortlistingCriteria();
                criteria.setId(rs.getInt("id"));
                criteria.setJobId(rs.getInt("job_id"));
                int minExp = rs.getInt("min_experience");
                if (!rs.wasNull()) {
                    criteria.setMinExperience(minExp);
                }
                criteria.setRequiredQualification(rs.getString("required_qualification"));
                criteria.setRequiredSkills(rs.getString("required_skills"));
                criteria.setOptionalLocation(rs.getString("optional_location"));
                criteria.setOptionalGrade(rs.getString("optional_grade"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    criteria.setCreatedAt(ts.toLocalDateTime());
                }
                return criteria;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
