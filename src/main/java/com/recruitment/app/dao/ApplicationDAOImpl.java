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
        return "Unknown"; // fallback if no user found
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
    @Override
    public void save(Application app) {
        String sql = "INSERT INTO applications (user_id, job_id, qualification, experience, cover_letter, status, applied_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, app.getUserId());
            ps.setInt(2, app.getJobId());
            ps.setString(3, app.getQualification());
            ps.setString(4, app.getExperience());
            ps.setString(5, app.getCoverLetter());
            ps.setString(6, "submitted");

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}