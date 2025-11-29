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

}
