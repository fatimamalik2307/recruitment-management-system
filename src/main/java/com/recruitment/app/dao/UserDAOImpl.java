package com.recruitment.app.dao;

import com.recruitment.app.config.DBConnection;
import com.recruitment.app.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * User DAO implementation.
 * - Keeps SQL details here (SRP).
 * - Other layers depend on this abstraction (DIP).
 */
public class UserDAOImpl implements UserDAO {

    @Override
    public boolean register(User user) {
        // Insert fields including role and company_id (company_id may be null)
        String sql = "INSERT INTO users(full_name, email, username, password, contact, role, status, company_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPassword()); // already hashed
            ps.setString(5, user.getContact());
            ps.setString(6, user.getRole());
            ps.setString(7, user.getStatus());
            if (user.getCompanyId() == null) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, user.getCompanyId());
            }

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User login(String username, String password) {
        // This method returns the user object (with stored hashed password) so caller can verify.
        // But to centralize verification we will return the user and let service verify hashed password.
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password")); // hashed password
                u.setContact(rs.getString("contact"));
                u.setRole(rs.getString("role"));
                u.setStatus(rs.getString("status"));
                int companyId = rs.getInt("company_id");
                if (!rs.wasNull()) u.setCompanyId(companyId);
                return u;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
        } catch (Exception e) {
            e.printStackTrace();
            // If error, return true to be safe (prevent duplicate role)
            return true;
        }
    }
}
