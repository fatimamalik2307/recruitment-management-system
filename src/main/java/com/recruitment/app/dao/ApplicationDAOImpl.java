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

    // -----------------------------------------------------
    // HM DECISION LOGIC (YOUR CODE)
    // -----------------------------------------------------
    @Override
    public boolean updateHMDecision(int applicationId, String decision) {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, decision);
            stmt.setInt(2, applicationId);
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
            stmt.setInt(1, jobId);
            stmt.setString(2, decision);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                applications.add(mapRow(rs));
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
            stmt.setInt(1, jobId);
            stmt.setString(2, decision);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // -----------------------------------------------------
    // CORE CRUD
    // -----------------------------------------------------
    @Override
    public void save(Application app) {
        String sql = "INSERT INTO applications (user_id, job_id, qualification, experience, cover_letter, status, applied_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, NOW())";

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

    @Override
    public Application getById(int applicationId) {
        String sql = "SELECT * FROM applications WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // -----------------------------------------------------
    // LIST QUERIES
    // -----------------------------------------------------
    @Override
    public List<Application> getApplicationsByJobId(int jobId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE job_id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Application> getApplicationsByUserId(int userId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE user_id = ? ORDER BY applied_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // -----------------------------------------------------
    // USER + JOB INFO
    // -----------------------------------------------------
    @Override
    public String getApplicantFullName(int userId) {
        String sql = "SELECT full_name FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getString("full_name");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    @Override
    public String getApplicantPhone(int userId) {
        String sql = "SELECT contact FROM users WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("contact");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getJobTitle(int jobId) {
        String sql = "SELECT job_title FROM jobs WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getString("job_title");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Job";
    }

    // -----------------------------------------------------
    // Utility Mapper
    // -----------------------------------------------------
    private Application mapRow(ResultSet rs) throws SQLException {
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
    @Override
    public List<Application> getFinalRankingsByJob(int jobId) {
        List<Application> list = new ArrayList<>();

        String sql = "SELECT * FROM applications WHERE job_id = ? ORDER BY final_score DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public String getApplicantEmail(int userId) {
        String sql = "SELECT email FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getString("email");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
