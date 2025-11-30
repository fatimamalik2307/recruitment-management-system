package com.recruitment.app.dao;

import com.recruitment.app.config.DBConnection;
import com.recruitment.app.models.ShortlistingCriteria;


import java.sql.*;
import java.time.LocalDateTime;

public class ShortlistingCriteriaDAOImpl implements ShortlistingCriteriaDAO {

    private final Connection conn;

    public ShortlistingCriteriaDAOImpl(Connection connection) {
        this.conn = DBConnection.getConnection();
    }

    @Override
    public ShortlistingCriteria save(ShortlistingCriteria criteria) {
        String sql = "INSERT INTO shortlisting_criteria " +
                "(job_id, min_experience, required_qualification, required_skills, optional_location, optional_grade, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, criteria.getJobId());

            if (criteria.getMinExperience() != null)
                ps.setInt(2, criteria.getMinExperience());
            else
                ps.setNull(2, Types.INTEGER);

            ps.setString(3, criteria.getRequiredQualification());
            ps.setString(4, criteria.getRequiredSkills());
            ps.setString(5, criteria.getOptionalLocation());
            ps.setString(6, criteria.getOptionalGrade());

            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                criteria.setId(rs.getInt("id"));
            }
            return criteria;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ShortlistingCriteria getById(int id) {
        String sql = "SELECT * FROM shortlisting_criteria WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return extract(rs);

        } catch (SQLException e) { e.printStackTrace(); }

        return null;
    }

    @Override
    public ShortlistingCriteria getByJobId(int jobId) {
        String sql = "SELECT * FROM shortlisting_criteria WHERE job_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return extract(rs);

        } catch (SQLException e) { e.printStackTrace(); }

        return null;
    }

    @Override
    public boolean update(ShortlistingCriteria criteria) {
        String sql = "UPDATE shortlisting_criteria " +
                "SET min_experience=?, required_qualification=?, required_skills=?, optional_location=?, optional_grade=? " +
                "WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            if (criteria.getMinExperience() != null)
                ps.setInt(1, criteria.getMinExperience());
            else
                ps.setNull(1, Types.INTEGER);

            ps.setString(2, criteria.getRequiredQualification());
            ps.setString(3, criteria.getRequiredSkills());
            ps.setString(4, criteria.getOptionalLocation());
            ps.setString(5, criteria.getOptionalGrade());
            ps.setInt(6, criteria.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM shortlisting_criteria WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); }

        return false;
    }

    private ShortlistingCriteria extract(ResultSet rs) throws SQLException {

        ShortlistingCriteria c = new ShortlistingCriteria();

        c.setId(rs.getInt("id"));
        c.setJobId(rs.getInt("job_id"));

        int minExp = rs.getInt("min_experience");
        if (!rs.wasNull()) c.setMinExperience(minExp);

        c.setRequiredQualification(rs.getString("required_qualification"));
        c.setRequiredSkills(rs.getString("required_skills"));
        c.setOptionalLocation(rs.getString("optional_location"));
        c.setOptionalGrade(rs.getString("optional_grade"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(ts.toLocalDateTime());

        return c;
    }
}
