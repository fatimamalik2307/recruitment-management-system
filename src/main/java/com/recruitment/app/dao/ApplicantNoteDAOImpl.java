package com.recruitment.app.dao;

import com.recruitment.app.models.ApplicantNote;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicantNoteDAOImpl implements ApplicantNoteDAO {
    private final Connection connection;

    public ApplicantNoteDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<ApplicantNote> findById(Long id) {
        String sql = "SELECT * FROM applicant_notes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding note by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ApplicantNote> findByApplicationId(Long applicationId) {
        String sql = "SELECT * FROM applicant_notes WHERE application_id = ? ORDER BY created_at DESC";
        List<ApplicantNote> notes = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, applicationId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding notes by application id", e);
        }
        return notes;
    }

    @Override
    public List<ApplicantNote> findByRecruiterId(Long recruiterId) {
        String sql = "SELECT * FROM applicant_notes WHERE recruiter_id = ? ORDER BY created_at DESC";
        List<ApplicantNote> notes = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, recruiterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding notes by recruiter id", e);
        }
        return notes;
    }

    @Override
    public List<ApplicantNote> findByApplicationAndRecruiter(Long applicationId, Long recruiterId) {
        String sql = "SELECT * FROM applicant_notes WHERE application_id = ? AND recruiter_id = ? ORDER BY created_at DESC";
        List<ApplicantNote> notes = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, applicationId);
            stmt.setLong(2, recruiterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding notes by application and recruiter", e);
        }
        return notes;
    }

    @Override
    public ApplicantNote save(ApplicantNote note) {
        String sql = "INSERT INTO applicant_notes " +
                "(application_id, recruiter_id, note_text, note_type, created_at, updated_at) " +
                "VALUES (?, ?, ? , ?::note_type_enum, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, note.getApplicationId());
            stmt.setLong(2, note.getRecruiterId());
            stmt.setString(3, note.getNoteText());
            stmt.setString(4, note.getNoteType().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating note failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    note.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating note failed, no ID obtained.");
                }
            }

            // Set timestamps in the object
            note.setCreatedAt(java.time.LocalDateTime.now());
            note.setUpdatedAt(java.time.LocalDateTime.now());

            return note;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving note", e);
        }
    }


    @Override
    public ApplicantNote update(ApplicantNote note) {
        String sql = "UPDATE applicant_notes SET note_text = ?, note_type = ?::note_type_enum, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, note.getNoteText());
            stmt.setString(2, note.getNoteType().name()); // still pass name()
            stmt.setLong(3, note.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating note failed, no rows affected.");
            }
            return note;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating note", e);
        }
    }


    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM applicant_notes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting note", e);
        }
    }

    @Override
    public boolean deleteByApplicationId(Long applicationId) {
        String sql = "DELETE FROM applicant_notes WHERE application_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, applicationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting notes by application id", e);
        }
    }

    private ApplicantNote mapResultSetToNote(ResultSet rs) throws SQLException {
        ApplicantNote note = new ApplicantNote();
        note.setId(rs.getLong("id"));
        note.setApplicationId(rs.getLong("application_id"));
        note.setRecruiterId(rs.getLong("recruiter_id"));
        note.setNoteText(rs.getString("note_text"));
        note.setNoteType(ApplicantNote.NoteType.valueOf(rs.getString("note_type")));
        note.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        note.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return note;
    }
    // Add to ApplicantNoteDAOImpl
    @Override
    public List<ApplicantNote> findByHiringManagerId(Long hiringManagerId) {
        List<ApplicantNote> notes = new ArrayList<>();
        String sql = "SELECT * FROM applicant_notes WHERE recruiter_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, hiringManagerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding notes by hiring manager id", e);
        }
        return notes;
    }

    @Override
    public List<ApplicantNote> findByApplicationAndType(Long applicationId, String noteType) {
        List<ApplicantNote> notes = new ArrayList<>();
        String sql = "SELECT * FROM applicant_notes WHERE application_id = ? AND note_type = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, applicationId);
            stmt.setString(2, noteType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding notes by application and type", e);
        }
        return notes;
    }

    @Override
    public int countNotesByApplication(Long applicationId) {
        String sql = "SELECT COUNT(*) FROM applicant_notes WHERE application_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, applicationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting notes by application", e);
        }
        return 0;
    }
}