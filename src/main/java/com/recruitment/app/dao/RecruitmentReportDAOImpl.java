package com.recruitment.app.dao;

import com.recruitment.app.config.DBConnection;
import com.recruitment.app.models.JobPosting;
import com.recruitment.app.models.RecruitmentReport;

import java.sql.*;

public class RecruitmentReportDAOImpl implements RecruitmentReportDAO {

    @Override
    public RecruitmentReport generateReport(int jobId, int recruiterId) {
        try (Connection conn = DBConnection.getConnection()) {

            // 1. Total applications
            String totalAppsQuery = "SELECT COUNT(*) FROM applications WHERE job_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(totalAppsQuery);
            ps1.setInt(1, jobId);
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();
            int totalApplications = rs1.getInt(1);

            // 2. Shortlisted applications
            String shortlistedQuery = "SELECT COUNT(*) FROM shortlist s " +
                    "JOIN applications a ON s.application_id = a.id " +
                    "WHERE a.job_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(shortlistedQuery);
            ps2.setInt(1, jobId);
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();
            int totalShortlisted = rs2.getInt(1);

            // 3. Fetch job details
            String jobQuery = "SELECT job_title, department, deadline FROM jobs WHERE id = ?";
            PreparedStatement psJob = conn.prepareStatement(jobQuery);
            psJob.setInt(1, jobId);
            ResultSet rsJob = psJob.executeQuery();

            String jobTitle = "";
            String department = "";
            Date postedOn = null;
            if (rsJob.next()) {
                jobTitle = rsJob.getString("job_title");
                department = rsJob.getString("department");
                postedOn = rsJob.getDate("deadline");
            }

            // 4. Insert the report
            String insertQuery = "INSERT INTO recruitment_reports(" +
                    "job_id, recruiter_id, total_applications, total_shortlisted, job_title, department, posted_on" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING *";
            PreparedStatement ps3 = conn.prepareStatement(insertQuery);
            ps3.setInt(1, jobId);
            ps3.setInt(2, recruiterId);
            ps3.setInt(3, totalApplications);
            ps3.setInt(4, totalShortlisted);
            ps3.setString(5, jobTitle);
            ps3.setString(6, department);
            ps3.setDate(7, postedOn);

            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) {
                RecruitmentReport report = new RecruitmentReport();
                report.setId(rs3.getInt("id"));
                report.setJobId(jobId);
                report.setRecruiterId(recruiterId);
                report.setGeneratedAt(rs3.getTimestamp("generated_at").toLocalDateTime());
                report.setTotalApplications(totalApplications);
                report.setTotalShortlisted(totalShortlisted);
                report.setJobTitle(rs3.getString("job_title"));
                report.setDepartment(rs3.getString("department"));

                Date reportPostedOn = rs3.getDate("posted_on");
                report.setPostedOn(reportPostedOn != null ? reportPostedOn.toString() : "");

                return report;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public RecruitmentReport getReportByJobId(int jobId) {
        try (Connection conn = DBConnection.getConnection()) {

            String query = "SELECT * FROM recruitment_reports WHERE job_id = ? ORDER BY generated_at DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, jobId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                RecruitmentReport report = new RecruitmentReport();
                report.setId(rs.getInt("id"));
                report.setJobId(rs.getInt("job_id"));
                report.setRecruiterId(rs.getInt("recruiter_id"));
                report.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                report.setTotalApplications(rs.getInt("total_applications"));
                report.setTotalShortlisted(rs.getInt("total_shortlisted"));
                report.setJobTitle(rs.getString("job_title"));
                report.setDepartment(rs.getString("department"));
                report.setPostedOn(rs.getDate("posted_on").toString());
                return report;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
