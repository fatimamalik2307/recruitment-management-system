package com.recruitment.app.dao;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of JobDAO using PostgreSQL.
 * Handles its own DB connection internally.
 */
public class JobDAOImpl implements JobDAO {

    private final Connection connection;

    public JobDAOImpl() {
        // Get connection internally
        this.connection = DBConnection.getConnection();
    }

    @Override
    public void addJob(JobPosting job) {
        String sql = "INSERT INTO jobs (job_title, department, description, required_qualification, " +
                "job_location, deadline, job_type, salary_range, status, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, job.getTitle());
            stmt.setString(2, job.getDepartment());
            stmt.setString(3, job.getDescription());
            stmt.setString(4, job.getRequiredQualification());
            stmt.setString(5, job.getJobLocation());
            stmt.setDate(6, job.getDeadline() != null ? Date.valueOf(job.getDeadline()) : null);
            stmt.setString(7, job.getJobType());
            stmt.setString(8, job.getSalaryRange());
            stmt.setString(9, job.getStatus());
            stmt.setInt(10, job.getCreatedBy());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<JobPosting> getAllJobs() {
        List<JobPosting> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JobPosting job = new JobPosting(
                        rs.getString("job_title"),
                        rs.getString("department"),
                        rs.getString("description"),
                        rs.getString("required_qualification"),
                        rs.getString("job_location"),
                        rs.getDate("deadline") != null ? rs.getDate("deadline").toLocalDate() : null,
                        rs.getString("job_type"),
                        rs.getString("salary_range"),
                        rs.getInt("created_by")
                );
                job.setId(rs.getInt("id"));
                job.setStatus(rs.getString("status"));
                jobs.add(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }

    @Override
    public List<JobPosting> getJobsByRecruiter(int recruiterId) {
        List<JobPosting> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE created_by = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recruiterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JobPosting job = new JobPosting(
                        rs.getString("job_title"),
                        rs.getString("department"),
                        rs.getString("description"),
                        rs.getString("required_qualification"),
                        rs.getString("job_location"),
                        rs.getDate("deadline") != null ? rs.getDate("deadline").toLocalDate() : null,
                        rs.getString("job_type"),
                        rs.getString("salary_range"),
                        rs.getInt("created_by")
                );
                job.setId(rs.getInt("id"));
                job.setStatus(rs.getString("status"));
                jobs.add(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }

    @Override
    public JobPosting getJobById(int jobId) {
        String sql = "SELECT * FROM jobs WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JobPosting job = new JobPosting(
                        rs.getString("job_title"),
                        rs.getString("department"),
                        rs.getString("description"),
                        rs.getString("required_qualification"),
                        rs.getString("job_location"),
                        rs.getDate("deadline") != null ? rs.getDate("deadline").toLocalDate() : null,
                        rs.getString("job_type"),
                        rs.getString("salary_range"),
                        rs.getInt("created_by")
                );
                job.setId(rs.getInt("id"));
                job.setStatus(rs.getString("status"));
                return job;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateJob(JobPosting job) {
        String sql = "UPDATE jobs SET job_title=?, department=?, description=?, required_qualification=?, " +
                "job_location=?, deadline=?, job_type=?, salary_range=?, status=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, job.getTitle());
            stmt.setString(2, job.getDepartment());
            stmt.setString(3, job.getDescription());
            stmt.setString(4, job.getRequiredQualification());
            stmt.setString(5, job.getJobLocation());
            stmt.setDate(6, job.getDeadline() != null ? Date.valueOf(job.getDeadline()) : null);
            stmt.setString(7, job.getJobType());
            stmt.setString(8, job.getSalaryRange());
            stmt.setString(9, job.getStatus());
            stmt.setInt(10, job.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteJob(int jobId) {
        String sql = "DELETE FROM jobs WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
