package com.recruitment.app.dao;

import com.recruitment.app.models.Application;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAOImpl implements ApplicationDAO {

    private final Connection conn;

    public ApplicationDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean updateHMDecision(int applicationId, String decision) {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, decision); // Using existing status field
            stmt.setInt(2, applicationId); // Changed to int
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Application> findByJobAndHmDecision(int jobId, String decision) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE job_id = ? AND status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId); // Changed to int
            stmt.setString(2, decision);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Application app = new Application();
                app.setId(rs.getInt("id"));
                app.setUserId(rs.getInt("user_id"));
                app.setJobId(rs.getInt("job_id"));
                app.setQualification(rs.getString("qualification"));
                app.setExperience(rs.getString("experience"));
                app.setCoverLetter(rs.getString("cover_letter"));
                app.setStatus(rs.getString("status"));
                app.setAppliedAt(rs.getTimestamp("applied_at").toLocalDateTime());
                applications.add(app);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public int countByJobAndDecision(int jobId, String decision) {
        String sql = "SELECT COUNT(*) FROM applications WHERE job_id = ? AND status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId); // Changed to int
            stmt.setString(2, decision);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Your existing methods remain the same
    @Override
    public List<Application> getApplicationsByJobId(int jobId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE job_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Application app = new Application();
                app.setId(rs.getInt("id"));
                app.setUserId(rs.getInt("user_id"));
                app.setJobId(rs.getInt("job_id"));
                app.setQualification(rs.getString("qualification"));
                app.setExperience(rs.getString("experience"));
                app.setCoverLetter(rs.getString("cover_letter"));
                app.setStatus(rs.getString("status"));
                app.setAppliedAt(rs.getTimestamp("applied_at").toLocalDateTime());
                list.add(app);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String getApplicantFullName(int userId) {
        String sql = "SELECT full_name FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    @Override
    public Application getById(int applicationId) {
        String sql = "SELECT * FROM applications WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Application app = new Application();
                app.setId(rs.getInt("id"));
                app.setUserId(rs.getInt("user_id"));
                app.setJobId(rs.getInt("job_id"));
                app.setQualification(rs.getString("qualification"));
                app.setExperience(rs.getString("experience"));
                app.setCoverLetter(rs.getString("cover_letter"));
                app.setStatus(rs.getString("status"));
                app.setAppliedAt(rs.getTimestamp("applied_at").toLocalDateTime());
                return app;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getApplicantPhone(int userId) {
        String sql = "SELECT contact FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("contact");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}