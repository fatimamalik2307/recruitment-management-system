package com.recruitment.app.dao;

import com.recruitment.app.config.DBConnection;
import com.recruitment.app.models.JobPosting;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAOImpl {

    public List<JobPosting> getAllJobs() {
        List<JobPosting> list = new ArrayList<>();

        String sql = "SELECT id, job_title, department, description, required_qualification, deadline FROM jobs ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
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

                list.add(job);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
