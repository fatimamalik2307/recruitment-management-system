package com.recruitment.app.dao;

import com.recruitment.app.models.JobDescription;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDescriptionDAOImpl implements JobDescriptionDAO {

    private final Connection conn;

    public JobDescriptionDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(JobDescription desc) {
        String sql =
                "INSERT INTO job_descriptions " +
                        "(recruiter_id, job_title, duties, responsibilities, job_purpose, " +
                        " reporting_structure, job_type, department,required_qualification) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, desc.getRecruiterId());
            ps.setString(2, desc.getTitle());
            ps.setString(3, desc.getDuties());
            ps.setString(4, desc.getResponsibilities());
            ps.setString(5, desc.getJobPurpose());
            ps.setString(6, desc.getReportingStructure());
            ps.setString(7, desc.getJobType());
            ps.setString(8, desc.getDepartment());
            ps.setString(9, desc.getRequiredQualification());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<JobDescription> getByRecruiter(int recruiterId) {
        List<JobDescription> list = new ArrayList<>();

        String sql =
                "SELECT * FROM job_descriptions " +
                        "WHERE recruiter_id = ? ORDER BY created_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recruiterId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                JobDescription d = new JobDescription();

                d.setId(rs.getInt("id"));
                d.setRecruiterId(rs.getInt("recruiter_id"));
                d.setTitle(rs.getString("job_title"));
                d.setDuties(rs.getString("duties"));
                d.setResponsibilities(rs.getString("responsibilities"));
                d.setJobPurpose(rs.getString("job_purpose"));
                d.setReportingStructure(rs.getString("reporting_structure"));

                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) d.setCreatedAt(ts.toLocalDateTime());

                d.setJobType(rs.getString("job_type"));
                d.setDepartment(rs.getString("department"));
                d.setRequiredQualification(rs.getString("required_qualification"));


                list.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
