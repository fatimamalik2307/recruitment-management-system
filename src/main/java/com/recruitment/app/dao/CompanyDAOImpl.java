package com.recruitment.app.dao;

import com.recruitment.app.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Company DAO - SRP: handles company persistence.
 */
public class CompanyDAOImpl implements CompanyDAO {

    @Override
    public int getOrCreateCompanyId(String companyName) {
        String findSql = "SELECT id FROM companies WHERE name = ?";
        String insertSql = "INSERT INTO companies(name) VALUES (?) RETURNING id";

        try (Connection conn = DBConnection.getConnection()) {
            // Check existing
            PreparedStatement psFind = conn.prepareStatement(findSql);
            psFind.setString(1, companyName);
            ResultSet rs = psFind.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            // Insert new
            PreparedStatement psInsert = conn.prepareStatement(insertSql);
            psInsert.setString(1, companyName);
            ResultSet rs2 = psInsert.executeQuery();
            if (rs2.next()) return rs2.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
