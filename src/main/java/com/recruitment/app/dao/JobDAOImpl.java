package com.recruitment.app.dao;

import com.recruitment.app.models.JobPosting;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobDAOImpl implements JobDAO {

    private final Connection conn;

    public JobDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void updateDeadline(int jobId, LocalDate newDeadline) {
        String sql = "UPDATE jobs SET deadline = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, newDeadline); // PostgreSQL supports LocalDate via setObject
            stmt.setInt(2, jobId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update job deadline", e);
        }
    }
    @Override
    public void addJob(JobPosting job) {
        String sql = """
        INSERT INTO jobs 
        (job_title, department, description, required_qualification, job_location,
         deadline, job_type, salary_range, status, recruiter_id, hiring_manager_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, job.getJobTitle());
            ps.setString(2, job.getDepartment());
            ps.setString(3, job.getDescription());
            ps.setString(4, job.getRequiredQualification());
            ps.setString(5, job.getJobLocation());

            if (job.getDeadline() != null)
                ps.setDate(6, java.sql.Date.valueOf(job.getDeadline()));
            else
                ps.setNull(6, Types.DATE);

            ps.setString(7, job.getJobType());
            ps.setString(8, job.getSalaryRange());
            ps.setString(9, job.getStatus());

            ps.setInt(10, job.getRecruiterId());
            ps.setInt(11, job.getHiringManagerId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateJob(JobPosting job) {
        String sql = "UPDATE jobs SET job_title=?, department=?, description=?, hiring_manager_id=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, job.getJobTitle());
            ps.setString(2, job.getDepartment());
            ps.setString(3, job.getDescription());
            ps.setInt(4, job.getHiringManagerId()); // Add this
            ps.setInt(5, job.getId());
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
    public void closeJob(int jobId) {
        String sql = "UPDATE jobs SET deadline = CURRENT_DATE - INTERVAL '1 day' WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            int updated = stmt.executeUpdate();
            System.out.println("DEBUG: closeJob executed, rows updated = " + updated);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to close job with ID " + jobId, e);
        }
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
        job.setTitle(rs.getString("job_title"));
        job.setDepartment(rs.getString("department"));
        job.setDescription(rs.getString("description"));
        job.setRequiredQualification(rs.getString("required_qualification"));
        job.setJobLocation(rs.getString("job_location"));

        // Deadline (convert SQL date → LocalDate)
        Date sqlDate = rs.getDate("deadline");
        if (sqlDate != null) {
            job.setDeadline(sqlDate.toLocalDate());
        }

        job.setJobType(rs.getString("job_type"));
        job.setSalaryRange(rs.getString("salary_range"));
        job.setStatus(rs.getString("status"));
        job.setCreatedBy(rs.getInt("created_by"));
        job.setRecruiterId(rs.getInt("recruiter_id"));
        job.setHiringManagerId(rs.getInt("hiring_manager_id"));

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
    @Override
    public List<JobPosting> getJobsForHMByCompany(int hmId) {
        List<JobPosting> jobs = new ArrayList<>();
        String sql = """
        SELECT j.id, j.job_title, j.department, j.status
        FROM jobs j
        JOIN users r ON j.recruiter_id = r.id
        JOIN users hm ON hm.id = ?
        WHERE r.company_id = hm.company_id AND r.role = 'Recruiter'
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hmId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JobPosting job = new JobPosting();
                job.setId(rs.getInt("id"));
                job.setJobTitle(rs.getString("job_title"));
                job.setDepartment(rs.getString("department"));
                job.setStatus(rs.getString("status"));
                jobs.add(job);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return jobs;
    }


    // In your JobDAOImpl class, replace the Long methods with int methods:



// Remove the findById(Long jobId) method since you already have getJobById(int jobId)
}
