package com.recruitment.app.dao;

import com.recruitment.app.models.Shortlist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShortlistDAOImpl implements ShortlistDAO {
    private final Connection conn;

    public ShortlistDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Shortlist save(Shortlist shortlist) {
        String sql = "INSERT INTO shortlist (criteria_id, application_id, shortlisted_at) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shortlist.getCriteriaId());
            stmt.setInt(2, shortlist.getApplicationId());
            stmt.setTimestamp(3, Timestamp.valueOf(shortlist.getShortlistedAt()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                shortlist.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shortlist;
    }

    @Override
    public List<Shortlist> getByJobId(int jobId) {
        String sql = "SELECT s.id, s.criteria_id, s.application_id, s.shortlisted_at " +
                "FROM shortlist s " +
                "JOIN shortlisting_criteria sc ON s.criteria_id = sc.id " +
                "WHERE sc.job_id = ?";
        List<Shortlist> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Shortlist s = new Shortlist();
                s.setId(rs.getInt("id"));
                s.setCriteriaId(rs.getInt("criteria_id"));
                s.setApplicationId(rs.getInt("application_id"));
                s.setShortlistedAt(rs.getTimestamp("shortlisted_at").toLocalDateTime());
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Shortlist getById(int id) {
        String sql = "SELECT * FROM shortlist WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Shortlist s = new Shortlist();
                s.setId(rs.getInt("id"));
                s.setCriteriaId(rs.getInt("criteria_id"));
                s.setApplicationId(rs.getInt("application_id"));
                s.setShortlistedAt(rs.getTimestamp("shortlisted_at").toLocalDateTime());
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM shortlist WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getApplicantNameByApplicationId(int applicationId) {
        String sql = "SELECT u.full_name " +
                "FROM applications a " +
                "JOIN users u ON a.user_id = u.id " +
                "WHERE a.id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}
