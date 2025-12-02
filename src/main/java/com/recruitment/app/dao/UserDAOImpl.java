package com.recruitment.app.dao;

import com.recruitment.app.config.DBConnection;
import com.recruitment.app.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean register(User user) {
        String sql = "INSERT INTO users(full_name, email, username, password, contact, role, status, company_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getContact());
            ps.setString(6, user.getRole());
            ps.setString(7, user.getStatus());
            if (user.getCompanyId() == null) ps.setNull(8, java.sql.Types.INTEGER);
            else ps.setInt(8, user.getCompanyId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, contact = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getContact());
            ps.setInt(4, user.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean roleExistsForCompany(String role, int companyId) {
        String sql = "SELECT 1 FROM users WHERE role = ? AND company_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, companyId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) { e.printStackTrace(); return true; }
    }

    /** ============================
     *  New methods
     * ============================ */

    @Override
    public User getById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToUser(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public User getByApplicationId(int applicationId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN applications a ON a.user_id = u.id " +
                "WHERE a.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToUser(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** ============================
     *  Utility
     * ============================ */
    private User mapResultSetToUser(ResultSet rs) throws Exception {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setContact(rs.getString("contact"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        int companyId = rs.getInt("company_id");
        if (!rs.wasNull()) u.setCompanyId(companyId);
        return u;
    }
    @Override
    public boolean updatePassword(int id, String hashedPassword) {
        String sql = "UPDATE users SET password=? WHERE id=?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
