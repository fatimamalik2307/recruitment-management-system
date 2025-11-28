package com.recruitment.app.dao;

import com.recruitment.app.models.User;
import com.recruitment.app.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean register(User user) {
        String sql = "INSERT INTO users(full_name,email,username,password,contact) VALUES (?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getContact());

            return ps.executeUpdate() > 0;
        }
        catch (Exception e) { e.printStackTrace(); return false; }
    }


    @Override
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setUsername(rs.getString("username"));
                u.setContact(rs.getString("contact"));
                u.setRole(rs.getString("role"));
                return u;
            }

        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }


    @Override
    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, contact=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getContact());
            ps.setInt(4, user.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
