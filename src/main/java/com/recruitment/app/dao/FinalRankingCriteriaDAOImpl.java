package com.recruitment.app.dao;

import com.recruitment.app.models.FinalRankingCriteria;

import java.sql.*;

public class FinalRankingCriteriaDAOImpl implements FinalRankingCriteriaDAO {

    private final Connection conn;

    public FinalRankingCriteriaDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(FinalRankingCriteria criteria) {
        String sql = "INSERT INTO final_ranking_criteria (job_id, technical_weight, hr_weight, criteria_weight) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, criteria.getJobId());
            stmt.setDouble(2, criteria.getTechnicalWeight());
            stmt.setDouble(3, criteria.getHrWeight());
            stmt.setDouble(4, criteria.getCriteriaWeight());
            boolean success = stmt.executeUpdate() > 0;
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) criteria.setId(rs.getInt(1));
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public FinalRankingCriteria getByJobId(int jobId) {
        String sql = "SELECT * FROM final_ranking_criteria WHERE job_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return new FinalRankingCriteria(
                        rs.getInt("job_id"),
                        rs.getDouble("technical_weight"),
                        rs.getDouble("hr_weight"),
                        rs.getDouble("criteria_weight")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(FinalRankingCriteria criteria) {
        String sql = "UPDATE final_ranking_criteria SET technical_weight=?, hr_weight=?, criteria_weight=? WHERE job_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, criteria.getTechnicalWeight());
            stmt.setDouble(2, criteria.getHrWeight());
            stmt.setDouble(3, criteria.getCriteriaWeight());
            stmt.setInt(4, criteria.getJobId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
