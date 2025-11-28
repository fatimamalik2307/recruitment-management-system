package com.recruitment.app.dao;

import com.recruitment.app.models.JobPosting;
import com.recruitment.app.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    public List<JobPosting> getAllJobs() {
        List<JobPosting> jobs = new ArrayList<>();

        String query = "SELECT id, job_title, department, description, required_qualification, deadline FROM jobs";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                JobPosting job = new JobPosting(
                        rs.getInt("id"),
                        rs.getString("job_title"),
                        rs.getString("department"),
                        rs.getString("description"),
                        rs.getString("required_qualification"),
                        rs.getString("deadline")
                );

                jobs.add(job);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobs;
    }
}
