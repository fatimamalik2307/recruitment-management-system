package com.recruitment.app.dao;

import com.recruitment.app.models.FinalRankedCandidate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FinalRankedCandidateDAOImpl implements FinalRankedCandidateDAO {

    private final Connection conn;

    public FinalRankedCandidateDAOImpl(Connection conn) {
        this.conn = conn;
    }

    // EXISTING METHODS
    @Override
    public boolean save(FinalRankedCandidate candidate) {
        String sql = "INSERT INTO final_ranked_candidates(application_id, job_id, composite_score, rank, status, generated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidate.getApplicationId());
            ps.setInt(2, candidate.getJobId());
            ps.setDouble(3, candidate.getCompositeScore());
            ps.setInt(4, candidate.getRank());
            ps.setString(5, candidate.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(candidate.getGeneratedAt()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<FinalRankedCandidate> getByJobId(int jobId) {
        List<FinalRankedCandidate> list = new ArrayList<>();
        String sql = "SELECT * FROM final_ranked_candidates WHERE job_id = ? ORDER BY rank ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FinalRankedCandidate f = new FinalRankedCandidate();
                f.setId(rs.getInt("id"));
                f.setApplicationId(rs.getInt("application_id"));
                f.setJobId(rs.getInt("job_id"));
                f.setCompositeScore(rs.getDouble("composite_score"));
                f.setRank(rs.getInt("rank"));
                f.setStatus(rs.getString("status"));
                f.setHmNotes(rs.getString("hm_notes"));
                f.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public FinalRankedCandidate getById(int id) {
        String sql = "SELECT * FROM final_ranked_candidates WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                FinalRankedCandidate f = new FinalRankedCandidate();
                f.setId(rs.getInt("id"));
                f.setApplicationId(rs.getInt("application_id"));
                f.setJobId(rs.getInt("job_id"));
                f.setCompositeScore(rs.getDouble("composite_score"));
                f.setRank(rs.getInt("rank"));
                f.setStatus(rs.getString("status"));
                f.setHmNotes(rs.getString("hm_notes"));
                f.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                return f;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateRankAndScore(int id, double compositeScore, int rank) {
        String sql = "UPDATE final_ranked_candidates SET composite_score = ?, rank = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, compositeScore);
            stmt.setInt(2, rank);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatusAndNotes(int id, String status, String hmNotes) {
        String sql = "UPDATE final_ranked_candidates SET status = ?, hm_notes = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, hmNotes);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM final_ranked_candidates WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // NEW HM METHODS - USING int INSTEAD OF Long
    @Override
    public List<FinalRankedCandidate> findByJobPostingIdAndStatus(int jobPostingId, String status) {
        List<FinalRankedCandidate> list = new ArrayList<>();
        String sql = "SELECT * FROM final_ranked_candidates WHERE job_id = ? AND status = ? ORDER BY rank ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobPostingId);
            stmt.setString(2, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FinalRankedCandidate f = new FinalRankedCandidate();
                f.setId(rs.getInt("id"));
                f.setApplicationId(rs.getInt("application_id"));
                f.setJobId(rs.getInt("job_id"));
                f.setCompositeScore(rs.getDouble("composite_score"));
                f.setRank(rs.getInt("rank"));
                f.setStatus(rs.getString("status"));
                f.setHmNotes(rs.getString("hm_notes"));
                f.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE final_ranked_candidates SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateStatusByJobPosting(int jobPostingId, String status) {
        String sql = "UPDATE final_ranked_candidates SET status = ? WHERE job_id = ? AND status = 'Ready for HM'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, jobPostingId);
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("[DEBUG DAO] Updated " + rowsUpdated + " rows for job ID: " + jobPostingId);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR DAO] Failed to update status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existsByJobPostingId(int jobPostingId) {
        String sql = "SELECT COUNT(*) FROM final_ranked_candidates WHERE job_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobPostingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<FinalRankedCandidate> findById(int id) {
        FinalRankedCandidate candidate = getById(id);
        return Optional.ofNullable(candidate);
    }

    @Override
    public Optional<FinalRankedCandidate> findByApplicationId(int applicationId) {
        String sql = "SELECT * FROM final_ranked_candidates WHERE application_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                FinalRankedCandidate f = new FinalRankedCandidate();
                f.setId(rs.getInt("id"));
                f.setApplicationId(rs.getInt("application_id"));
                f.setJobId(rs.getInt("job_id"));
                f.setCompositeScore(rs.getDouble("composite_score"));
                f.setRank(rs.getInt("rank"));
                f.setStatus(rs.getString("status"));
                f.setHmNotes(rs.getString("hm_notes"));
                f.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                return Optional.of(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<FinalRankedCandidate> findByHiringManagerId(int hiringManagerId) {
        List<FinalRankedCandidate> list = new ArrayList<>();
        String sql = "SELECT frc.* FROM final_ranked_candidates frc " +
                "JOIN jobs j ON frc.job_id = j.id " +
                "WHERE j.hiring_manager_id = ? ORDER BY frc.rank ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hiringManagerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FinalRankedCandidate f = new FinalRankedCandidate();
                f.setId(rs.getInt("id"));
                f.setApplicationId(rs.getInt("application_id"));
                f.setJobId(rs.getInt("job_id"));
                f.setCompositeScore(rs.getDouble("composite_score"));
                f.setRank(rs.getInt("rank"));
                f.setStatus(rs.getString("status"));
                f.setHmNotes(rs.getString("hm_notes"));
                f.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Helper method to map ResultSet to FinalRankedCandidate (if needed)
    private FinalRankedCandidate mapResultSetToCandidate(ResultSet rs) throws SQLException {
        FinalRankedCandidate candidate = new FinalRankedCandidate();
        candidate.setId(rs.getInt("id"));
        candidate.setApplicationId(rs.getInt("application_id"));
        candidate.setJobId(rs.getInt("job_id"));
        candidate.setCompositeScore(rs.getDouble("composite_score"));
        candidate.setRank(rs.getInt("rank"));
        candidate.setStatus(rs.getString("status"));
        candidate.setHmNotes(rs.getString("hm_notes"));
        candidate.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        return candidate;
    }
    public boolean hasRecruiterSubmittedFinalList(int jobId) {
        String sql = "SELECT COUNT(*) FROM final_ranked_candidates WHERE job_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    public List<FinalRankedCandidate> getFinalCandidatesForHM(int jobId) {
        List<FinalRankedCandidate> list = new ArrayList<>();
        String sql = "SELECT * FROM final_ranked_candidates WHERE job_id = ? ORDER BY rank ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FinalRankedCandidate f = new FinalRankedCandidate();
                f.setId(rs.getInt("id"));
                f.setApplicationId(rs.getInt("application_id"));
                f.setJobId(rs.getInt("job_id"));
                f.setCompositeScore(rs.getDouble("composite_score"));
                f.setRank(rs.getInt("rank"));
                f.setStatus(rs.getString("status"));
                f.setHmNotes(rs.getString("hm_notes"));
                f.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


}