package com.recruitment.app.dao;

import com.recruitment.app.models.AssessmentResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssessmentResultDAOImpl implements AssessmentResultDAO {
    private final Connection conn;

    public AssessmentResultDAOImpl(Connection conn) {
        this.conn = conn;
    }
    private AssessmentResult mapResultSet(ResultSet rs) throws SQLException {
        AssessmentResult a = new AssessmentResult();
        a.setId(rs.getInt("id"));
        a.setShortlistId(rs.getInt("shortlist_id"));
        a.setRecruiterId(rs.getInt("recruiter_id"));
        a.setTechnicalScore(rs.getDouble("technical_score"));
        a.setHrScore(rs.getDouble("hr_score"));
        a.setRemarks(rs.getString("remarks"));
        a.setRecordedAt(rs.getTimestamp("recorded_at").toLocalDateTime());
        return a;
    }

    @Override
    public AssessmentResult save(AssessmentResult assessment) {
        String sql = "INSERT INTO assessment_results (shortlist_id, recruiter_id, technical_score, hr_score, remarks, recorded_at) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assessment.getShortlistId());
            stmt.setInt(2, assessment.getRecruiterId());
            stmt.setDouble(3, assessment.getTechnicalScore());
            stmt.setDouble(4, assessment.getHrScore());
            stmt.setString(5, assessment.getRemarks());
            stmt.setTimestamp(6, Timestamp.valueOf(assessment.getRecordedAt()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) assessment.setId(rs.getInt("id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assessment;
    }

    @Override
    public List<AssessmentResult> getByShortlistId(int shortlistId) {
        List<AssessmentResult> list = new ArrayList<>();
        String sql = "SELECT * FROM assessment_results WHERE shortlist_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shortlistId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AssessmentResult a = new AssessmentResult();
                a.setId(rs.getInt("id"));
                a.setShortlistId(rs.getInt("shortlist_id"));
                a.setRecruiterId(rs.getInt("recruiter_id"));
                a.setTechnicalScore(rs.getDouble("technical_score"));
                a.setHrScore(rs.getDouble("hr_score"));
                a.setRemarks(rs.getString("remarks"));
                a.setRecordedAt(rs.getTimestamp("recorded_at").toLocalDateTime());
                list.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<AssessmentResult> getByJobId(int jobId) {
        List<AssessmentResult> list = new ArrayList<>();
        String sql = "SELECT ar.* FROM assessment_results ar " +
                "JOIN shortlist s ON ar.shortlist_id = s.id " +
                "JOIN applications a ON s.application_id = a.id " +
                "WHERE a.job_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public void delete(int id) {
        String sql = "DELETE FROM assessment_results WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
