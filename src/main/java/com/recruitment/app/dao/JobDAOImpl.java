package com.recruitment.app.dao;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAOImpl implements JobDAO {

    private final Connection conn;

    public JobDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void addJob(JobPosting job) {
        // set recruiterId from logged-in user
        job.setRecruiterId(SessionManager.loggedInUser.getId());

        String sql = "INSERT INTO jobs (job_title, department, description, recruiter_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, job.getJobTitle());
            ps.setString(2, job.getDepartment());
            ps.setString(3, job.getDescription());
            ps.setInt(4, job.getRecruiterId()); // just get it, don't call set here
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateJob(JobPosting job) {
        String sql = "UPDATE jobs SET job_title=?, department=?, description=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, job.getJobTitle());
            ps.setString(2, job.getDepartment());
            ps.setString(3, job.getDescription());
            ps.setInt(4, job.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteJob(int jobId) {
        String sql = "DELETE FROM jobs WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JobPosting getJobById(int jobId) {
        String sql = "SELECT * FROM jobs WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToJob(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<JobPosting> getAllJobs() {
        List<JobPosting> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }


    @Override
    public List<JobPosting> getJobsByRecruiterId(int recruiterId) {
        List<JobPosting> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE recruiter_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recruiterId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }

    @Override
    public List<JobPosting> getJobsByRecruiterAndStatus(int recruiterId, String status) {
        List<JobPosting> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE recruiter_id=? AND status=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recruiterId);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }

    public String getUserFullName(int userId) {
        String sql = "SELECT full_name FROM users WHERE id=?";
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

    private JobPosting mapResultSetToJob(ResultSet rs) throws SQLException {
        JobPosting job = new JobPosting();
        job.setId(rs.getInt("id"));
        job.setJobTitle(rs.getString("job_title"));
        job.setDepartment(rs.getString("department"));
        job.setDescription(rs.getString("description"));
        job.setRecruiterId(rs.getInt("recruiter_id"));
        // Optional: set status if available in DB
        if (columnExists(rs, "status")) {
            job.setStatus(rs.getString("status"));
        }
        return job;
    }

    private boolean columnExists(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
