package com.recruitment.app.dao;

import com.recruitment.app.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Company DAO - SRP: handles company persistence.
 */
public class CompanyDAOImpl implements CompanyDAO {
    public int getOrCreateCompanyId(String name) {
        try (Connection conn = DBConnection.getConnection()) {

            // 1. CHECK if company already exists
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM companies WHERE name = ?");
            check.setString(1, name);
            ResultSet rs = check.executeQuery();
            if (rs.next()) return rs.getInt("id");

            // 2. INSERT new company
            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO companies (name) VALUES (?) RETURNING id");
            insert.setString(1, name);
            ResultSet rs2 = insert.executeQuery();
            if (rs2.next()) return rs2.getInt("id");

        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

}
